package com.nminh.app.controller.router;

import java.io.IOException;

public interface ScreenRouter {

    public void onLeftSideBarItemClick(String screenName);

    public void newWindowWithParentController(String screenNameAction, Object data, Object controller) throws IOException;

    public void newTabWithParent(String screenNameAction, Object data, Object controller);
}
