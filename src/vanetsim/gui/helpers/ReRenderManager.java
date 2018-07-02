package vanetsim.gui.helpers;

import vanetsim.gui.Renderer;

public final class ReRenderManager extends Thread {

    private static final ReRenderManager INSTANCE = new ReRenderManager();

    private boolean doRender_ = false;

    private ReRenderManager() {
    }

    public static ReRenderManager getInstance() {
        return INSTANCE;
    }

    public void doReRender() {
        doRender_ = true;
    }

    public void run() {
        setName("ReRenderManager");
        setPriority(Thread.MIN_PRIORITY);
        Renderer renderer = Renderer.getInstance();
        while (true) {
            if (doRender_) {
                doRender_ = false;
                renderer.ReRender(true, false);
            }
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
            ;
        }
    }
}
