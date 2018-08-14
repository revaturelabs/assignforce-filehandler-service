package com.revature.assignforce.assignforcefilehandler.model;

import java.io.File;

public class FileData {
    private Metadata metadata;
    private File file;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
