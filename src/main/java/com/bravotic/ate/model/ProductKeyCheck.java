package com.bravotic.ate.model;

public class ProductKeyCheck implements TextPromptAction {
    private final AState state;
    private final APrompt keyCheck;
    private final APrompt goodKey;
    private final APrompt badKey;

    private boolean checkFirst(String key) {
        switch(key) {
            case "333":
            case "444":
            case "555":
            case "666":
            case "777":
            case "888":
            case "999":
                return false;
            default:
                return true;
        }
    }

    private boolean mod7Check(String key) {
        int total = 0;
        for (char ch : key.toCharArray()) {
            if (ch >= '0' && ch <= '9') {
                total += ch - '0';
            }
            else {
                System.out.println("Failed mod 7");
                return false;
            }

        }
        System.out.println("Total is " + total);
        return total % 7 == 0;
    }


    private boolean keyCheck(String key) {
        return checkFirst(key.substring(0, 3)) && mod7Check(key.substring(4));
    }
    public ProductKeyCheck(AState state) {
        this.state = state;
        this.keyCheck = new APrompt("Please enter a product key to use Text Editor: ", APrompt.QUESTION, null, this);

        this.goodKey = new APrompt("Thank you, you may now use Text Editor", APrompt.INFO, new String[]{"Okay"}, new TextPromptAction() {
            @Override
            public void submitted(String text) {
                state.setView(AState.VIEW_DOCUMENT);
            }

            @Override
            public void closed() {
                state.setView(AState.VIEW_DOCUMENT);
            }
        });

        this.badKey = new APrompt("This key is invalid, please try again.", APrompt.ERROR, new String[]{"Okay", "Fuck off"}, new TextPromptAction() {
            @Override
            public void submitted(String text) {
                state.showPrompt(keyCheck);
            }

            @Override
            public void closed() {
                state.showPrompt(keyCheck);
            }
        });

        state.showPrompt(keyCheck);
    }

    @Override
    public void submitted(String text) {
        if (keyCheck(text)) {
            state.showPrompt(goodKey);
        }
        else {
            state.showPrompt(badKey);
        }
    }

    @Override
    public void closed() {
        state.setView(AState.VIEW_MENU);
    }
}
