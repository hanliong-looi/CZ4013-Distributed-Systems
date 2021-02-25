package cz4013.shared.serialize;

import java.nio.ByteBuffer;

/**
 * Interface for classes which implements custom serialization/deserialization.
 */
public interface Serializable {
  void deserialize(ByteBuffer buf);

  void serialize(ByteBuffer buf);
}


