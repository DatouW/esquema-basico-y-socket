package tcp.server;

import java.io.Serializable;

public class FileLog implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String path;

    public FileLog(String id, String path) {
        this.id = id;
        this.path = path;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

}
