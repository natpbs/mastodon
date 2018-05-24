package org.mastodon.app;

import java.io.IOException;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.mastodon.revised.bdv.SharedBigDataViewerData;
import org.mastodon.revised.mamut.MamutProject;
import org.mastodon.revised.mamut.MamutProjectIO;
import org.mastodon.revised.mamut.WindowManager;
import org.mastodon.revised.model.mamut.Model;
import org.mastodon.revised.model.mamut.Spot;
import org.mastodon.spatial.SpatialIndex;
import org.mastodon.spatial.SpatioTemporalIndex;
import org.scijava.Context;

import bdv.util.Affine3DHelpers;
import bdv.viewer.Source;
import ij.ImageJ;
import ij.ImagePlus;
import mpicbg.spim.data.SpimDataException;
import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class ExportToLabeling
{

	public static void main( final String[] args ) throws IOException, SpimDataException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		Locale.setDefault( Locale.ROOT );
		System.setProperty( "apple.laf.useScreenMenuBar", "true" );
		final Context context = new Context();

		final String projectFile = "../TrackMate3/samples/mamutproject";
		final WindowManager windowManager = new WindowManager( context );
		final MamutProject project = new MamutProjectIO().load( projectFile );
		windowManager.getProjectManager().open( project );

		final Model model = windowManager.getAppModel().getModel();
		final SpatioTemporalIndex< Spot > spatioTemporalIndex = model.getSpatioTemporalIndex();

		final SharedBigDataViewerData data = windowManager.getAppModel().getSharedBdvData();
		final int numTimepoints = data.getNumTimepoints();
		final Source< ? > source = data.getSources().get( 0 ).getSpimSource();
		final long[] dims = new long[ 4 ];
		for ( int d = 0; d < 3; d++ )
			dims[ d ] = source.getSource( 0, 0 ).dimension( d );
		dims[ 3 ] = numTimepoints;
		final ArrayImg< UnsignedIntType, IntArray > img = ArrayImgs.unsignedInts( dims );

		final AffineTransform3D transform = new AffineTransform3D();
		final EllipsoidInsideTest test = new EllipsoidInsideTest();

		// Real-values bounds.
		final double[] lMin = new double[ 3 ];
		final double[] lMax = new double[ 3 ];
		// Integer-values bounds.
		final long[] pMin = new long[ 3 ];
		final long[] pMax = new long[ 3 ];
		// Real-values global coordinates - spot position.
		final double[] gPos = new double[ 3 ];
		final double[] lPos = new double[ 3 ];
		// Bounds in global coordinates.
		final double[] gMin = new double[ 3 ];
		final double[] gMax = new double[ 3 ];

		for ( int t = 0; t < numTimepoints; t++ )
		{
			source.getSourceTransform( t, 0, transform );
			final IntervalView< UnsignedIntType > volume = Views.hyperSlice( img, 3, t );
			final RandomAccess< UnsignedIntType > ra = volume.randomAccess( volume );

			final SpatialIndex< Spot > si = spatioTemporalIndex.getSpatialIndex( t );
			for ( final Spot spot : si )
			{
				// No conflict with label 0.
				final int id = 1 + spot.getInternalPoolIndex();

				// Make bounding box.
				// In physical coordinates.
				final double maxR = Math.sqrt( spot.getBoundingSphereRadiusSquared() );
				spot.localize( gMin );
				spot.localize( gMax );
				for ( int d = 0; d < gMax.length; d++ )
				{
					gMin[ d ] -= maxR;
					gMax[ d ] += maxR;
				}

				// Transform to pixel coordinates.
				transform.applyInverse( lMin, gMin );
				transform.applyInverse( lMax, gMax );
				for ( int d = 0; d < pMax.length; d++ )
				{
					pMin[ d ] = Math.round( lMin[ d ] );
					pMax[ d ] = Math.round( lMax[ d ] );
				}
				final FinalInterval bb = new FinalInterval( pMin, pMax );

				// Iterate over BB.
				final IntervalView< UnsignedIntType > interval = Views.interval( volume, bb );
				final Cursor< UnsignedIntType > cursor = interval.localizingCursor();
				while ( cursor.hasNext() )
				{
					cursor.fwd();
					if ( !Intervals.contains( volume, cursor ) )
						continue;

					cursor.localize( lPos );
					transform.apply( lPos, gPos );
					if ( test.isPointInside( gPos, spot ) )
					{
						ra.setPosition( cursor );
						ra.get().set( id );
					}
				}
			}
		}

		ImageJ.main( args );
		final ImagePlus imp = ImageJFunctions.wrap( img, "Labels" );
		imp.setDimensions( 1, ( int ) dims[ 2 ], numTimepoints );
		imp.getCalibration().pixelWidth = Affine3DHelpers.extractScale( transform, 0 );
		imp.getCalibration().pixelHeight = Affine3DHelpers.extractScale( transform, 1 );
		imp.getCalibration().pixelDepth = Affine3DHelpers.extractScale( transform, 2 );
		imp.getCalibration().setUnit( source.getVoxelDimensions().unit() );
		imp.show();
	}
}
