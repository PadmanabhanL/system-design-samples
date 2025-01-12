package com.app.index.bo;

public class KeyValueMetadata {

    private String fileName;

    private long keyByteOffset;

    private int keyByteLength;

    private long valueByteOffset;

    private int valueByteLength;

    public KeyValueMetadata(String fileName,
                            long keyByteOffset,
                            int keyByteLength,
                            long valueByteOffset,
                            int valueByteLength) {
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

    public int getKeyByteLength() {
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

    public int getValueByteLength() {
        return valueByteLength;
    }

    public void setValueByteLength(int valueByteLength) {
        this.valueByteLength = valueByteLength;
    }
}
