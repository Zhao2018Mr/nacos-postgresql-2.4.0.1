package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.mapper.HistoryConfigInfoMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

/**
 *  The mysql implementation of ConfigInfoMapper.
 *
 *  @author fuhouyu
 */
public class HistoryConfigInfoMapperByPostgres extends AbstractMapperByPostgres implements HistoryConfigInfoMapper {

    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }

    @Override
    public MapperResult removeConfigHistory(MapperContext context) {
        String sql = "DELETE FROM his_config_info WHERE id IN (SELECT id FROM his_config_info WHERE gmt_modified < ? LIMIT ?)";
        return new MapperResult(sql, CollectionUtils.list(context.getWhereParameter("startTime"), context.getWhereParameter("limitSize")));
    }

    @Override
    public MapperResult pageFindConfigHistoryFetchRows(MapperContext context) {
        String sql = "SELECT nid,data_id,group_id,tenant_id,app_name,src_ip,src_user,op_type,gmt_create,gmt_modified FROM his_config_info WHERE data_id = ? AND group_id = ? AND tenant_id = ? ORDER BY nid DESC  OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize();
        return new MapperResult(sql, CollectionUtils.list(context.getWhereParameter("dataId"), context.getWhereParameter("groupId"), context.getWhereParameter("tenantId")));
    }
}
