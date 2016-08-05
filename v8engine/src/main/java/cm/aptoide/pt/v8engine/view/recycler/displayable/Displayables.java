/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import cm.aptoide.pt.v8engine.interfaces.LifecycleSchim;

import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;

/**
 * Created by neuro on 18-04-2016.
 */
public class Displayables implements LifecycleSchim {

	private final List<Displayable> displayables = new LinkedList<>();

	public Displayables() {
	}

	public void add(int position, Displayable displayable) {
		// Ignore empty displayables
		if (displayable instanceof EmptyDisplayable) {
			return;
		}

		if (displayable instanceof DisplayableGroup) {
			add(position, ((DisplayableGroup) displayable).getChildren());
		} else {
			displayables.add(position, displayable);
		}
	}

	public void add(int position, Collection<? extends Displayable> collection) {
		for (Displayable displayable : collection) {
			add(position, displayable);
		}
	}

	public void add(Displayable displayable) {
		// Ignore empty displayables
		if (displayable instanceof EmptyDisplayable) {
			return;
		}

		if (displayable instanceof DisplayableGroup) {
			add(((DisplayableGroup) displayable).getChildren());
		} else {
			displayables.add(displayable);
		}
	}


	public void add(Collection<? extends Displayable> collection) {
		for (Displayable displayable : collection) {
			add(displayable);
		}
	}

	public Displayable pop() {
		if (displayables.size() > 0) {
			return displayables.remove(displayables.size() - 1);
		} else {
			return null;
		}
	}

	public Displayable get(Integer position) {
		if (displayables.size() > position) {
			return displayables.get(position);
		} else {
			return null;
		}
	}

	public int size() {
		return displayables.size();
	}

	public void clear() {
		displayables.clear();
	}

	//
	// LifecycleSchim interface
	//

	public void onResume() {
		for (final Displayable displayable : displayables) {
			displayable.onResume();
		}
	}

	public void onPause() {
		for (final Displayable displayable : displayables) {
			displayable.onPause();
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		for (final Displayable displayable : displayables) {
			displayable.onSaveInstanceState(outState);
		}
	}

	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		for (final Displayable displayable : displayables) {
			displayable.onViewStateRestored(savedInstanceState);
		}
	}
}
