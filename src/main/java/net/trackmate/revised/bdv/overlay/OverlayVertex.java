package net.trackmate.revised.bdv.overlay;

import net.imglib2.RealLocalizable;
import net.trackmate.graph.zzgraphinterfaces.Vertex;
import net.trackmate.graph.zzrefcollections.Ref;
import net.trackmate.spatial.HasTimepoint;

public interface OverlayVertex< O extends OverlayVertex< O, E >, E extends OverlayEdge< E, ? > >
		extends Vertex< E >, Ref< O >, RealLocalizable, HasTimepoint
{
	public boolean isSelected();

	public void getCovariance( final double[][] mat );

	public double getBoundingSphereRadiusSquared();
}
