package com.bridge.console.utils.result;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import org.springframework.data.domain.*;

import java.util.Iterator;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-21 21:11
 */
public class PageUtil {

    /**
     * @param content  分页结果中content中实际包含的内容
     * @param pageable 分页条件，直接使用controller中的pageable，
     * @param total
     * @param <D>
     * @return
     */
    public static <D> Page<D> newPage(List<D> content, Pageable pageable, int total) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        PageRequest pageRequest = PageRequest.of((pageNumber < 1 ? 1 : pageNumber) - 1, pageSize < 1 ? 1 : pageSize, pageable.getSort());
        return new PageImpl<>(content, pageRequest, total);
    }

    /**
     * @param param    需要设置分页条件的dao查询条件
     * @param pageable 分页条件，直接使用controller中的pageable
     */
    public static <T extends BasePageQueryParam> void fillParam(T param, Pageable pageable) {
        Iterator<String> it = null;
        if (null != pageable.getSort()) {
            // 设置排序参数
            it = Iterators.transform(pageable.getSort().iterator(), new Function<Sort.Order, String>() {
                @Override
                public String apply(Sort.Order order) {
                    return order.getProperty() + " " + order.getDirection().name();
                }
            });

        }
        // 设置分页参数
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        PageRequest pageRequest = PageRequest.of((pageNumber < 1 ? 1 : pageNumber) - 1, pageSize < 1 ? 1 : pageSize, pageable.getSort());
        param.setSorts(it == null ? null : Lists.newArrayList(it));
        param.setLimit(pageRequest.getPageSize());
        param.setOffset((int) pageRequest.getOffset());
    }
}
