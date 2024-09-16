package com.bravotic.cam.model;

public interface CMenuContainerListener {
    void submenuOpened(String label, CMenu menu);

    void submenuClosed(CMenu menu);

    void menuWouldLikeToClose();
}
