package net.runelite.mixins;

import net.runelite.api.mixins.Copy;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Replace;
import net.runelite.api.mixins.Shadow;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSFrames;
import net.runelite.rs.api.RSModel;
import net.runelite.rs.api.RSSequenceDefinition;

@Mixin(RSSequenceDefinition.class)
public abstract class SequenceDefinitionMixin implements RSSequenceDefinition {

  @Shadow("client")
  private static RSClient client;

  @Copy("applyTransformations")
  @Replace("applyTransformations")
  @SuppressWarnings("InfiniteRecursion")
  public RSModel copy$applyTransformations(RSModel model, int actionFrame,
      RSSequenceDefinition poseSeq, int poseFrame) {
    // reset frame ids because we're not interpolating this
    if (actionFrame < 0) {
      int packed = actionFrame ^ Integer.MIN_VALUE;
      actionFrame = packed & 0xFFFF;
    }
    if (poseFrame < 0) {
      int packed = poseFrame ^ Integer.MIN_VALUE;
      poseFrame = packed & 0xFFFF;
    }
    return copy$applyTransformations(model, actionFrame, poseSeq, poseFrame);
  }

  @Copy("transformActorModel")
  @Replace("transformActorModel")
  public RSModel copy$transformActorModel(RSModel model, int frame) {
    // check if the frame has not been modified
    if (frame >= 0) {
      return copy$transformActorModel(model, frame);
    }

    // remove flag to check if the frame has been modified
    int packed = frame ^ Integer.MIN_VALUE;
    int interval = packed >> 16;
    frame = packed & 0xFFFF;
    int nextFrame = frame + 1;
    if (nextFrame >= getFrameIDs().length) {
      // dont interpolate last frame
      nextFrame = -1;
    }
    int[] frameIds = getFrameIDs();
    int frameId = frameIds[frame];
    RSFrames frames = client.getFrames(frameId >> 16);
    int frameIdx = frameId & 0xFFFF;

    int nextFrameIdx = -1;
    RSFrames nextFrames = null;
    if (nextFrame != -1) {
      int nextFrameId = frameIds[nextFrame];
      nextFrames = client.getFrames(nextFrameId >> 16);
      nextFrameIdx = nextFrameId & 0xFFFF;
    }

    if (frames == null) {
      // not sure what toSharedModel does but it is needed
      return model.toSharedModel(true);
    }

    RSModel animatedModel = model.toSharedModel(!frames.getFrames()[frameIdx].isShowing());
    animatedModel.interpolateFrames(frames, frameIdx, nextFrames, nextFrameIdx, interval,
        getFrameLengths()[frame]);
    return animatedModel;
  }

  @Copy("transformObjectModel")
  @Replace("transformObjectModel")
  public RSModel copy$transformObjectModel(RSModel model, int frame, int rotation) {
    // check if the frame has not been modified
    if (frame >= 0) {
      return copy$transformObjectModel(model, frame, rotation);
    }

    // remove flag to check if the frame has been modified
    int packed = frame ^ Integer.MIN_VALUE;
    int interval = packed >> 16;
    frame = packed & 0xFFFF;

    int nextFrame = frame + 1;
    if (nextFrame >= getFrameIDs().length) {
      // dont interpolate last frame
      nextFrame = -1;
    }
    int[] frameIds = getFrameIDs();
    int frameId = frameIds[frame];
    RSFrames frames = client.getFrames(frameId >> 16);
    int frameIdx = frameId & 0xFFFF;

    int nextFrameIdx = -1;
    RSFrames nextFrames = null;
    if (nextFrame != -1) {
      int nextFrameId = frameIds[nextFrame];
      nextFrames = client.getFrames(nextFrameId >> 16);
      nextFrameIdx = nextFrameId & 0xFFFF;
    }

    if (frames == null) {
      return model.toSharedModel(true);
    }

    RSModel animatedModel = model.toSharedModel(!frames.getFrames()[frameIdx].isShowing());
    // reset rotation before animating
    rotation &= 3;
    if (rotation == 1) {
      animatedModel.rotateY270Ccw$api();
    } else if (rotation == 2) {
      animatedModel.rotateY180Ccw();
    } else if (rotation == 3) {
      animatedModel.rotateY90Ccw$api();
    }
    animatedModel.interpolateFrames(frames, frameIdx, nextFrames, nextFrameIdx, interval,
        getFrameLengths()[frame]);
    // reapply rotation after animating
    if (rotation == 1) {
      animatedModel.rotateY90Ccw$api();
    } else if (rotation == 2) {
      animatedModel.rotateY180Ccw();
    } else if (rotation == 3) {
      animatedModel.rotateY270Ccw$api();
    }
    return animatedModel;
  }

  @Copy("transformSpotAnimationModel")
  @Replace("transformSpotAnimationModel")
  public RSModel copy$transformSpotAnimationModel(RSModel model, int frame) {
    // check if the frame has not been modified
    if (frame >= 0) {
      return copy$transformSpotAnimationModel(model, frame);
    }

    // remove flag to check if the frame has been modified
    int packed = frame ^ Integer.MIN_VALUE;
    int interval = packed >> 16;
    frame = packed & 0xFFFF;
    int nextFrame = frame + 1;
    if (nextFrame >= getFrameIDs().length) {
      // dont interpolate last frame
      nextFrame = -1;
    }
    int[] frameIds = getFrameIDs();
    int frameId = frameIds[frame];
    RSFrames frames = client.getFrames(frameId >> 16);
    int frameIdx = frameId & 0xFFFF;

    int nextFrameIdx = -1;
    RSFrames nextFrames = null;
    if (nextFrame != -1) {
      int nextFrameId = frameIds[nextFrame];
      nextFrames = client.getFrames(nextFrameId >> 16);
      nextFrameIdx = nextFrameId & 0xFFFF;
    }

    if (frames == null) {
      return model.toSharedSpotAnimModel(true);
    }

    RSModel animatedModel = model.toSharedSpotAnimModel(!frames.getFrames()[frameIdx].isShowing());
    animatedModel.interpolateFrames(frames, frameIdx, nextFrames, nextFrameIdx, interval,
        getFrameLengths()[frame]);
    return animatedModel;
  }

  @Copy("transformWidgetModel")
  @Replace("transformWidgetModel")
  public RSModel copy$transformWidgetModel(RSModel model, int frame) {
    // check if the frame has not been modified
    if (frame >= 0) {
      return copy$transformWidgetModel(model, frame);
    }

    // remove flag to check if the frame has been modified
    int packed = frame ^ Integer.MIN_VALUE;
    int interval = packed >> 16;
    frame = packed & 0xFFFF;

    int nextFrame = frame + 1;
    if (nextFrame >= getFrameIDs().length) {
      // dont interpolate last frame
      nextFrame = -1;
    }
    int[] frameIds = getFrameIDs();
    int frameId = frameIds[frame];
    RSFrames frames = client.getFrames(frameId >> 16);
    int frameIdx = frameId & 0xFFFF;

    int nextFrameIdx = -1;
    RSFrames nextFrames = null;
    if (nextFrame != -1) {
      int nextFrameId = frameIds[nextFrame];
      nextFrames = client.getFrames(nextFrameId >> 16);
      nextFrameIdx = nextFrameId & 0xFFFF;
    }

    if (frames == null) {
      return model.toSharedModel(true);
    }

    RSFrames chatFrames = null;
    int chatFrameIdx = 0;
    if (getChatFrameIds() != null && frame < getChatFrameIds().length) {
      int chatFrameId = getChatFrameIds()[frame];
      chatFrames = client.getFrames(chatFrameId >> 16);
      chatFrameIdx = chatFrameId & 0xFFFF;
    }
    if (chatFrames != null && chatFrameIdx != 0xFFFF) {
      RSFrames nextChatFrames = null;
      int nextChatFrameIdx = -1;
      if (nextFrame != -1 && nextFrame < getChatFrameIds().length) {
        int chatFrameId = getChatFrameIds()[nextFrame];
        nextChatFrames = client.getFrames(chatFrameId >> 16);
        nextChatFrameIdx = chatFrameId & 0xFFFF;
      }
      // not sure if this can even happen but the client checks for this so to be safe
      if (nextChatFrameIdx == 0xFFFF) {
        nextChatFrames = null;
      }
      RSModel animatedModel = model.toSharedModel(!frames.getFrames()[frameIdx].isShowing()
          & !chatFrames.getFrames()[chatFrameIdx].isShowing());
      animatedModel.interpolateFrames(frames, frameIdx, nextFrames, nextFrameIdx, interval,
          getFrameLengths()[frame]);
      animatedModel.interpolateFrames(chatFrames, chatFrameIdx, nextChatFrames, nextChatFrameIdx,
          interval, getFrameLengths()[frame]);
      return animatedModel;
    }

    RSModel animatedModel = model.toSharedModel(!frames.getFrames()[frameIdx].isShowing());
    animatedModel.interpolateFrames(frames, frameIdx, nextFrames, nextFrameIdx, interval,
        getFrameLengths()[frame]);
    return animatedModel;
  }
}
