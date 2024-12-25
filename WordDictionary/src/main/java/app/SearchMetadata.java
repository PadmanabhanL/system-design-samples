package app;

public class SearchMetadata {
    private long byteOffset;

    private int length;

    public SearchMetadata(long byteOffset, int length) {
        this.byteOffset = byteOffset;
        this.length = length;
    }

    public long getByteOffset() {
        return byteOffset;
    }

    public void setByteOffset(long byteOffset) {
        this.byteOffset = byteOffset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Metadata{" +
               "byteOffset=" + byteOffset +
               ", length=" + length +
               '}';
    }
}
