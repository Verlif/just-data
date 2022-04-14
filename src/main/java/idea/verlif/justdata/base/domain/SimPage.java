package idea.verlif.justdata.base.domain;

import java.util.List;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/1/5 15:57
 */
public class SimPage<T> {

    protected List<T> records;
    protected long total;
    protected long size;
    protected long current;
    protected long pages;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }
}
