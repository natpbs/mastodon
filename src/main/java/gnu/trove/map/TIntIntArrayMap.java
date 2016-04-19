package gnu.trove.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.Constants;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;

public class TIntIntArrayMap implements TIntIntMap
{

	/**
	 * Int value for no key.
	 */
	private static final int NO_ENTRY_KEY = -1;

	/** the int value that represents null */
	private final int no_entry_value;

	private final TIntArrayList keyToObjMap;

	private int size;

	/*
	 * CONSTRUCTORS
	 */

	public TIntIntArrayMap()
	{
		this( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_INT_NO_ENTRY_VALUE );
	}

	public TIntIntArrayMap( final int initialCapacity, final int no_entry_value )
	{
		this.keyToObjMap = new TIntArrayList( initialCapacity );
		this.size = 0;
		this.no_entry_value = no_entry_value;
	}

	/*
	 * METHODS
	 */

	@Override
	public int getNoEntryKey()
	{
		return NO_ENTRY_KEY;
	}

	@Override
	public int getNoEntryValue()
	{
		return no_entry_value;
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public boolean isEmpty()
	{
		return size == 0;
	}

	@Override
	public boolean containsKey( final int key )
	{
		return keyToObjMap.size() > key && keyToObjMap.get( key ) != no_entry_value;
	}

	@Override
	public boolean containsValue( final int value )
	{
		return keyToObjMap.contains( value );
	}

	@Override
	public int get( final int key )
	{
		if ( keyToObjMap.size() <= key )
			return no_entry_value;

		return keyToObjMap.get( key );
	}

	@Override
	public int put( final int key, final int value )
	{
		while ( key >= keyToObjMap.size() )
			keyToObjMap.add( no_entry_value );

		final int old = keyToObjMap.set( key, value );
		if ( no_entry_value != old )
		{
			return old;
		}
		else
		{
			++size;
			return no_entry_value;
		}
	}

	@Override
	public int putIfAbsent( final int key, final int value )
	{
		if ( containsKey( key ) )
			return get( key );
		put( key, value );
		return no_entry_value;
	}

	@Override
	public boolean increment( final int key )
	{
		final int val = get( key );
		if ( val == no_entry_value )
			return false;
		keyToObjMap.set( key, val + 1 );
		return true;
	}

	@Override
	public boolean adjustValue( final int key, final int amount )
	{
		final int val = get( key );
		if ( val == no_entry_value )
			return false;
		keyToObjMap.set( key, val + amount );
		return true;
	}

	@Override
	public int adjustOrPutValue( final int key, final int adjust_amount, final int put_amount )
	{
		final int val = get( key );
		if ( val == no_entry_value )
		{
			keyToObjMap.set( key, put_amount );
			return put_amount;
		}
		else
		{
			keyToObjMap.set( key, val + adjust_amount );
			return val + adjust_amount;
		}
	}

	@Override
	public int remove( final int key )
	{
		if ( containsKey( key ) )
		{
			size--;
			return put( key, no_entry_value );
		}
		return no_entry_value;
	}

	@Override
	public void clear()
	{
		keyToObjMap.clear();
		size = 0;
	}

	@Override
	public void putAll( final Map< ? extends Integer, ? extends Integer > m )
	{
		for ( final int key : m.keySet() )
			put( key, m.get( key ) );
	}

	@Override
	public void putAll( final TIntIntMap m )
	{
		final TIntIterator it = m.keySet().iterator();
		while ( it.hasNext() )
		{
			final int key = it.next();
			put( key, m.get( key ) );
		}
	}

	@Override
	public TIntSet keySet()
	{
		return new KeySetView();
	}

	@Override
	public int[] keys()
	{
		final int[] array = new int[ size ];
		return keys( array );
	}

	@Override
	public int[] keys( final int[] array )
	{
		final int[] arr;
		if ( array.length < size() )
		{
			arr = new int[ size() ];
		}
		else
		{
			arr = array;
		}
		int index = 0;
		for ( int i = 0; i < keyToObjMap.size(); i++ )
		{
			final int val = keyToObjMap.get( i );
			if ( val == no_entry_value )
				continue;
			arr[ index++ ] = i;
		}
		for ( int i = index; i < array.length; i++ )
		{
			arr[ i ] = NO_ENTRY_KEY;
		}
		return arr;
	}

	@Override
	public ValueCollection valueCollection()
	{
		return new ValueCollection();
	}

	@Override
	public int[] values()
	{
		return valueCollection().toArray();
	}

	@Override
	public int[] values( final int[] array )
	{
		return valueCollection().toArray( array );
	}

	@Override
	public TIntIntIterator iterator()
	{
		return new TIntIntIterator()
		{
			private int cursor = -1;

			@Override
			public void advance()
			{
				cursor++;
				while ( keyToObjMap.get( cursor ) == no_entry_value )
				{
					cursor++;
				}
			}

			@Override
			public boolean hasNext()
			{
				int explorer = cursor + 1;
				while ( explorer < keyToObjMap.size() )
				{
					if ( keyToObjMap.get( explorer ) != no_entry_value )
						return true;
					explorer++;
				}
				return false;
			}

			@Override
			public void remove()
			{
				TIntIntArrayMap.this.remove( cursor );
			}

			@Override
			public int key()
			{
				return cursor;
			}

			@Override
			public int value()
			{
				return keyToObjMap.get( cursor );
			}

			@Override
			public int setValue( final int val )
			{
				return put( cursor, val );
			}
		};
	}

	@Override
	public boolean forEachKey( final TIntProcedure procedure )
	{
		return keySet().forEach( procedure );
	}

	@Override
	public boolean forEachValue( final TIntProcedure procedure )
	{
		return valueCollection().forEach( procedure );
	}

	@Override
	public boolean forEachEntry( final TIntIntProcedure procedure )
	{
		for ( int i = 0; i < keyToObjMap.size(); i++ )
		{
			final int val = keyToObjMap.get( i );
			if ( val == no_entry_value )
				continue;
			final boolean ok = procedure.execute( i, val );
			if ( !ok )
				return false;
		}
		return true;
	}

	@Override
	public void transformValues( final TIntFunction function )
	{
		for ( int i = 0; i < keyToObjMap.size(); i++ )
		{
			final int val = keyToObjMap.get( i );
			if ( val == no_entry_value )
				continue;
			keyToObjMap.set( i, function.execute( val ) );
		}
	}

	@Override
	public boolean retainEntries( final TIntIntProcedure procedure )
	{
		final TIntIntIterator it = iterator();
		boolean changed = false;
		while ( it.hasNext() )
		{
			it.advance();
			if ( !procedure.execute( it.key(), it.value() ) )
			{
				it.remove();
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public String toString()
	{
		if ( size < 1 )
			return super.toString() + " {}";

		final StringBuilder str = new StringBuilder();
		str.append( super.toString() );
		str.append( " { " );
		final int[] keys = keys();
		str.append( keys[ 0 ] + " -> " + get( keys[ 0 ] ) );
		for ( int i = 1; i < keys.length; i++ )
		{
			final int key = keys[ i ];
			str.append( ", " + key + " -> " + get( key ) );
		}
		str.append( " }" );
		return str.toString();
	}

	/*
	 * PRIVATE CLASSES
	 */

	private final class KeySetView implements TIntSet
	{

		@Override
		public int getNoEntryValue()
		{
			return NO_ENTRY_KEY;
		}

		@Override
		public int size()
		{
			return size;
		}

		@Override
		public boolean isEmpty()
		{
			return size == 0;
		}

		@Override
		public boolean contains( final int entry )
		{
			if ( entry == NO_ENTRY_KEY )
				return false;
			return keyToObjMap.size() > entry && keyToObjMap.get( entry ) != no_entry_value;
		}

		@Override
		public TIntIterator iterator()
		{
			return new TIntIterator()
			{

				/**
				 * Index of element to be returned by subsequent call to next.
				 */
				private int cursor = 0;

				/**
				 * Index of element returned by most recent call to next or
				 * previous. Reset to -1 if this element is deleted by a call to
				 * remove.
				 */
				int lastRet = -1;

				/** {@inheritDoc} */
				@Override
				public boolean hasNext()
				{
					return cursor < keyToObjMap.size();
				}

				/** {@inheritDoc} */
				@Override
				public int next()
				{
					try
					{
						while ( keyToObjMap.get( cursor ) == no_entry_value )
						{
							cursor++;
						}
						final int next = cursor;
						lastRet = cursor++;
						// Advance to next now.
						while ( cursor < keyToObjMap.size() && keyToObjMap.get( cursor ) == no_entry_value )
						{
							cursor++;
						}
						if ( cursor >= keyToObjMap.size() )
							cursor = Integer.MAX_VALUE;
						return next;
					}
					catch ( final IndexOutOfBoundsException e )
					{
						throw new NoSuchElementException();
					}
				}

				/** {@inheritDoc} */
				@Override
				public void remove()
				{
					if ( lastRet == -1 )
						throw new IllegalStateException();

					try
					{
						TIntIntArrayMap.this.remove( lastRet );
						if ( lastRet < cursor )
							cursor--;
						lastRet = -1;
					}
					catch ( final IndexOutOfBoundsException e )
					{
						throw new ConcurrentModificationException();
					}
				}
			};
		}

		@Override
		public int[] toArray()
		{
			return keys();
		}

		@Override
		public int[] toArray( final int[] dest )
		{
			return keys( dest );
		}

		@Override
		public boolean add( final int entry )
		{
			throw new UnsupportedOperationException( "add is not supported for keyset view." );
		}

		@Override
		public boolean remove( final int entry )
		{
			final int removed = TIntIntArrayMap.this.remove( entry );
			return ( removed != no_entry_value );
		}

		@Override
		public boolean containsAll( final Collection< ? > collection )
		{
			final Iterator< ? > it = collection.iterator();
			while ( it.hasNext() )
			{
				final Object obj = it.next();
				if ( !( obj instanceof Integer ) )
					return false;

				if ( !TIntIntArrayMap.this.containsKey( ( Integer ) obj ) )
					return false;
			}
			return true;
		}

		@Override
		public boolean containsAll( final TIntCollection collection )
		{
			final TIntIterator it = collection.iterator();
			while ( it.hasNext() )
			{
				if ( !TIntIntArrayMap.this.containsKey( it.next() ) )
					return false;
			}
			return true;
		}

		@Override
		public boolean containsAll( final int[] array )
		{
			for ( final int key : array )
			{
				if ( !TIntIntArrayMap.this.containsKey( key ) )
					return false;
			}
			return true;
		}

		@Override
		public boolean addAll( final Collection< ? extends Integer > collection )
		{
			throw new UnsupportedOperationException( "addAll is not supported for keyset view." );
		}

		@Override
		public boolean addAll( final TIntCollection collection )
		{
			throw new UnsupportedOperationException( "addAll is not supported for keyset view." );
		}

		@Override
		public boolean addAll( final int[] array )
		{
			throw new UnsupportedOperationException( "addAll is not supported for keyset view." );
		}

		@Override
		public boolean retainAll( final Collection< ? > collection )
		{
			boolean changed = false;
			for ( final int entry : keys() )
			{
				if ( !collection.contains( entry ) )
				{
					final int removed = TIntIntArrayMap.this.remove( entry );
					if ( removed != no_entry_value )
						changed = true;
				}
			}
			return changed;
		}

		@Override
		public boolean retainAll( final TIntCollection collection )
		{
			boolean changed = false;
			for ( final int entry : keys() )
			{
				if ( !collection.contains( entry ) )
				{
					final int removed = TIntIntArrayMap.this.remove( entry );
					if ( removed != no_entry_value )
						changed = true;
				}
			}
			return changed;
		}

		@Override
		public boolean retainAll( final int[] array )
		{
			boolean changed = false;
			for ( final int entry : keys() )
			{
				boolean found = false;
				for ( final int in : array )
				{
					if ( entry == in )
					{
						found = true;
						break;
					}
				}
				if ( !found )
				{
					final int removed = TIntIntArrayMap.this.remove( entry );
					if ( removed != no_entry_value )
						changed = true;
				}
			}
			return changed;
		}

		@Override
		public boolean removeAll( final Collection< ? > collection )
		{
			boolean changed = false;
			for ( final Object obj : collection )
			{
				if ( obj instanceof Integer )
				{
					final boolean removed = remove( ( int ) obj );
					if ( removed )
						changed = true;
				}
			}
			return changed;
		}

		@Override
		public boolean removeAll( final TIntCollection collection )
		{
			boolean changed = false;
			final TIntIterator it = collection.iterator();
			while ( it.hasNext() )
			{
				final int entry = it.next();
				final boolean removed = remove( entry );
				if ( removed )
					changed = true;
			}
			return changed;
		}

		@Override
		public boolean removeAll( final int[] array )
		{
			boolean changed = false;
			for ( final int entry : array )
			{
				final boolean removed = remove( entry );
				if ( removed )
					changed = true;
			}
			return changed;
		}

		@Override
		public void clear()
		{
			TIntIntArrayMap.this.clear();
		}

		@Override
		public boolean forEach( final TIntProcedure procedure )
		{
			for ( int i = 0; i < keyToObjMap.size(); i++ )
			{
				final int val = keyToObjMap.get( i );
				if ( val == no_entry_value )
					continue;
				final boolean ok = procedure.execute( i );
				if ( !ok )
					return false;
			}
			return true;
		}
	}

	private class ValueCollection implements TIntCollection
	{

		@Override
		public boolean add( final int value )
		{
			throw new UnsupportedOperationException( "add is not supported for valueCollection view." );
		}

		@Override
		public boolean addAll( final Collection< ? extends Integer > collection )
		{
			throw new UnsupportedOperationException( "add is not supported for valueCollection view." );
		}

		@Override
		public boolean addAll( final TIntCollection collection )
		{
			throw new UnsupportedOperationException( "add is not supported for valueCollection view." );
		}

		@Override
		public boolean addAll( final int[] array )
		{
			throw new UnsupportedOperationException( "add is not supported for valueCollection view." );
		}

		@Override
		public void clear()
		{
			TIntIntArrayMap.this.clear();
		}

		@Override
		public boolean contains( final int value )
		{
			return TIntIntArrayMap.this.containsValue( value );
		}

		@Override
		public boolean containsAll( final Collection< ? > collection )
		{
			for ( final Object element : collection )
			{
				if ( element instanceof Integer )
				{
					final int ele = ( ( Integer ) element ).intValue();
					if ( !TIntIntArrayMap.this.containsValue( ele ) ) { return false; }
				}
				else
				{
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean containsAll( final TIntCollection collection )
		{
			final TIntIterator iter = collection.iterator();
			while ( iter.hasNext() )
			{
				if ( !TIntIntArrayMap.this.containsValue( iter.next() ) ) { return false; }
			}
			return true;
		}

		@Override
		public boolean containsAll( final int[] array )
		{
			for ( final int element : array )
			{
				if ( !TIntIntArrayMap.this.containsKey( element ) ) { return false; }
			}
			return true;
		}

		@Override
		public boolean isEmpty()
		{
			return TIntIntArrayMap.this.isEmpty();
		}

		@Override
		public TIntIterator iterator()
		{
			return new TIntIterator()
			{

				/**
				 * Index of element to be returned by subsequent call to next.
				 */
				private int cursor = 0;

				/**
				 * Index of element returned by most recent call to next or
				 * previous. Reset to -1 if this element is deleted by a call to
				 * remove.
				 */
				int lastRet = -1;

				/** {@inheritDoc} */
				@Override
				public boolean hasNext()
				{
					return cursor < keyToObjMap.size();
				}

				/** {@inheritDoc} */
				@Override
				public int next()
				{
					try
					{
						while ( keyToObjMap.get( cursor ) == no_entry_value )
						{
							cursor++;
						}
						final int next = keyToObjMap.get( cursor );
						lastRet = cursor++;
						// Advance to next now.
						while ( cursor < keyToObjMap.size() && keyToObjMap.get( cursor ) == no_entry_value )
						{
							cursor++;
						}
						if ( cursor >= keyToObjMap.size() )
							cursor = Integer.MAX_VALUE;

						return next;
					}
					catch ( final IndexOutOfBoundsException e )
					{
						throw new NoSuchElementException();
					}
				}

				/** {@inheritDoc} */
				@Override
				public void remove()
				{
					if ( lastRet == -1 )
						throw new IllegalStateException();

					try
					{
						TIntIntArrayMap.this.remove( lastRet );
						if ( lastRet < cursor )
							cursor--;
						lastRet = -1;
					}
					catch ( final IndexOutOfBoundsException e )
					{
						throw new ConcurrentModificationException();
					}
				}
			};
		}

		@Override
		public boolean remove( final int obj )
		{
			final int key = keyToObjMap.indexOf( obj );
			if ( key < 0 )
				return false;

			--size;
			keyToObjMap.set( key, no_entry_value );
			return true;
		}

		@Override
		public boolean removeAll( final Collection< ? > collection )
		{
			boolean changed = false;
			for ( final Object element : collection )
			{
				if ( element instanceof Integer )
				{
					final int c = ( ( Integer ) element ).intValue();
					if ( remove( c ) )
					{
						changed = true;
					}
				}
			}
			return changed;
		}

		@Override
		public boolean removeAll( final TIntCollection collection )
		{
			if ( this == collection )
			{
				clear();
				return true;
			}
			boolean changed = false;
			final TIntIterator iter = collection.iterator();
			while ( iter.hasNext() )
			{
				final int element = iter.next();
				if ( remove( element ) )
				{
					changed = true;
				}
			}
			return changed;
		}

		@Override
		public boolean removeAll( final int[] array )
		{
			boolean changed = false;
			for ( int i = array.length; i-- > 0; )
			{
				if ( remove( array[ i ] ) )
				{
					changed = true;
				}
			}
			return changed;
		}

		@Override
		public boolean retainAll( final Collection< ? > collection )
		{
			boolean modified = false;
			final TIntIterator iter = iterator();
			while ( iter.hasNext() )
			{
				if ( !collection.contains( Integer.valueOf( iter.next() ) ) )
				{
					iter.remove();
					modified = true;
				}
			}
			return modified;
		}

		@Override
		public boolean retainAll( final TIntCollection collection )
		{
			if ( this == collection ) { return false; }
			boolean modified = false;
			final TIntIterator iter = iterator();
			while ( iter.hasNext() )
			{
				if ( !collection.contains( iter.next() ) )
				{
					iter.remove();
					modified = true;
				}
			}
			return modified;
		}

		@Override
		public boolean retainAll( final int[] array )
		{
			boolean modified = false;
			final int[] copy = array.clone();
			Arrays.sort( copy );
			final TIntIterator iter = iterator();
			while ( iter.hasNext() )
			{
				if ( Arrays.binarySearch( copy, iter.next() ) < 0 )
				{
					iter.remove();
					modified = true;
				}
			}
			return modified;
		}

		@Override
		public int size()
		{
			return TIntIntArrayMap.this.size();
		}

		@Override
		public int getNoEntryValue()
		{
			return no_entry_value;
		}

		@Override
		public int[] toArray()
		{
			return toArray( new int[ size() ] );
		}

		@Override
		public int[] toArray( final int[] a )
		{
			final int[] arr;
			if ( a.length < size() )
			{
				arr = new int[ size() ];
			}
			else
			{
				arr = a;
			}

			int i = 0;
			for ( final int key : keys() )
			{
				arr[ i++ ] = get( key );
			}
			// nullify the rest.
			for ( int j = i; j < arr.length; j++ )
			{
				arr[ j ] = no_entry_value;
			}
			return arr;
		}

		@Override
		public boolean forEach( final TIntProcedure procedure )
		{
			for ( int i = 0; i < keyToObjMap.size(); i++ )
			{
				final int val = keyToObjMap.get( i );
				if ( val == no_entry_value )
					continue;
				final boolean ok = procedure.execute( val );
				if ( !ok )
					return false;
			}
			return true;
		}
	}
}
