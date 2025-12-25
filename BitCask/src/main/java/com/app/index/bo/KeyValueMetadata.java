package com.app.index.bo;

public class KeyValueMetadata {

    private String fileName;

    private long keyByteOffset;

    private long keyByteLength;

    private long valueByteOffset;

    private long valueByteLength;

    public KeyValueMetadata(String fileName,
                            long keyByteOffset,
                            long keyByteLength,
                            long valueByteOffset,
                            long valueByteLength) {
        this.fileName = fileName;
        this.keyByteOffset = keyByteOffset;
        this.keyByteLength = keyByteLength;
        this.valueByteOffset = valueByteOffset;
        this.valueByteLength = valueByteLength;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getKeyByteOffset() {
        return keyByteOffset;
    }

    public void setKeyByteOffset(long keyByteOffset) {
        this.keyByteOffset = keyByteOffset;
    }

    public long getKeyByteLength() {
        return keyByteLength;
    }

    public void setKeyByteLength(int keyByteLength) {
        this.keyByteLength = keyByteLength;
    }

    public long getValueByteOffset() {
        return valueByteOffset;
    }

    public void setValueByteOffset(long valueByteOffset) {
        this.valueByteOffset = valueByteOffset;
    }

    public long getValueByteLength() {
        return valueByteLength;
    }

    public void setValueByteLength(int valueByteLength) {
        this.valueByteLength = valueByteLength;
    }

    @Override
    public String toString() {
        return "{" + this.fileName + " " + this.keyByteOffset + " " + this.keyByteLength + " " + this.valueByteOffset + " " + this.valueByteLength + "}";
    }
}
