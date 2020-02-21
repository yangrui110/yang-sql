package top.sanguohf.top.bootcon.page;

import lombok.Data;

@Data
public class Page {
    public Page() {
    }

    public Page(int page, int size) {
        this.page = page;
        this.size = size;
    }

    private int page;
    private int size;
}
