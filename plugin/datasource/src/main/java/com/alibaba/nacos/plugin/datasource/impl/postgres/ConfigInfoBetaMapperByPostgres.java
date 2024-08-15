package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoBetaMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.util.ArrayList;
import java.util.List;

/**
 * The mysql implementation of ConfigInfoBetaMapper.
 * @author fuhouyu
 */
public class ConfigInfoBetaMapperByPostgres extends AbstractMapperByPostgres implements ConfigInfoBetaMapper {
    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }

    @Override
    public MapperResult findAllConfigInfoBetaForDumpAllFetchRows(MapperContext context) {
        int startRow = context.getStartRow();
        int pageSize = context.getPageSize();
        String sql = " SELECT t.id,data_id,group_id,tenant_id,app_name,content,md5,gmt_modified,beta_ips,encrypted_data_key" +
                "  FROM ( SELECT id FROM config_info_beta  ORDER BY id OFFSET " + startRow + " LIMIT " + pageSize + " )  g, config_info_beta t WHERE g.id = t.id ";
        List<Object> paramList = new ArrayList<>(2);
        paramList.add(startRow);
        paramList.add(pageSize);
        return new MapperResult(sql, paramList);
    }
}
