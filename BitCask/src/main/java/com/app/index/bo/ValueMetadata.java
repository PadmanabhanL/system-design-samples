package com.app.index.bo;

public class ValueMetadata {

    private String fileName;

    private long byteOffset;

    private int byteLength;

    public ValueMetadata(String fileName, long byteOffset, int byteLength) {
        this.fileName = fileName;
        this.byteOffset = byteOffset;
        this.byteLength = byteLength;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getByteOffset() {
        return byteOffset;
    }

    public void setByteOffset(long byteOffset) {
        this.byteOffset = byteOffset;
    }

    public int getByteLength() {
        return byteLength;
    }

    public void setByteLength(int byteLength) {
        this.byteLength = byteLength;
    }

    @Override
    public String toString() {
        return "ValueMetadata{" +
               "fileName='" + fileName + '\'' +
               ", byteOffset=" + byteOffset +
               ", byteLength=" + byteLength +
               '}';
    }
}
