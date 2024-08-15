package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.mapper.AbstractMapper;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoAggrMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.List;

/**
 * The postgresql implementation of ConfigInfoAggrMapper.
 * @author fuhouyu
 */
public class ConfigInfoAggrMapperByPostgres extends AbstractMapper implements ConfigInfoAggrMapper {

    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }

    @Override
    public String getFunction(String s) {
        return "";
    }

    @Override
    public MapperResult findConfigInfoAggrByPageFetchRows(MapperContext context) {
        int startRow = context.getStartRow();
        int pageSize = context.getPageSize();
        String dataId = (String) context.getWhereParameter("dataId");
        String groupId = (String) context.getWhereParameter("groupId");
        String tenantId = (String) context.getWhereParameter("tenantId");
        String sql = "SELECT data_id,group_id,tenant_id,datum_id,app_name,content FROM config_info_aggr WHERE data_id= ? AND group_id= ? AND tenant_id= ? ORDER BY datum_id OFFSET " + startRow + " LIMIT " + pageSize;
        List<Object> paramList = CollectionUtils.list(dataId, groupId, tenantId);
        return new MapperResult(sql, paramList);
    }
}
