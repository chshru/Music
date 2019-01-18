package com.chshru.music.ui.view.visualizer;

import android.content.Context;
import android.view.View;

import com.chshru.music.ui.view.visualizer.multiple.renderer.Renderer;

/**
 * Created by abc on 19-1-17.
 */

public abstract class VisualizerView extends View {

    public VisualizerView(Context context) {
        super(context);
    }

    public abstract void addRenderer(Renderer renderer);

    public abstract void clearRenderers();

    public abstract  boolean hasRenders();

    public abstract void release();

    public abstract void create(int session);

}
