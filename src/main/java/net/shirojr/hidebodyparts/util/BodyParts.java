package net.shirojr.hidebodyparts.util;

public enum BodyParts {
    HEAD("head"),
    HAT("hat"),
    BODY("body"),
    RIGHT_ARM("r_arm"),
    LEFT_ARM("l_arm"),
    RIGHT_LEG("r_leg"),
    LEFT_LEG("l_leg");

    private final String bodyPart;

    private BodyParts(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    public String getBodyPartName() {
        return this.bodyPart;
    }
}
