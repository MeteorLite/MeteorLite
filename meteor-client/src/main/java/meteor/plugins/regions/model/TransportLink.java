package meteor.plugins.regions.model;

import lombok.Value;

@Value
public class TransportLink {
    String source;
    String destination;
    String action;
    String objName;
    int objId;

    @Override
    public String toString() {
        return source + " " + destination + " " + action + " " + objName + " " + objId;
    }
}
