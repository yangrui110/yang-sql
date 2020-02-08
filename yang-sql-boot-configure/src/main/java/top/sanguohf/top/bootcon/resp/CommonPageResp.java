package top.sanguohf.top.bootcon.resp;

import lombok.Data;

@Data
public class CommonPageResp<T> {

    public T data;
    public long count;

}
