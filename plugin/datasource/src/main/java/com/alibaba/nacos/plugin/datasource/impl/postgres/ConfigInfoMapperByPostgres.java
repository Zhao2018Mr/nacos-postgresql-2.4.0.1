package com.alibaba.nacos.plugin.datasource.impl.postgres;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.NamespaceUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.plugin.datasource.constants.DataSourceConstant;
import com.alibaba.nacos.plugin.datasource.mapper.ConfigInfoMapper;
import com.alibaba.nacos.plugin.datasource.model.MapperContext;
import com.alibaba.nacos.plugin.datasource.model.MapperResult;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  The mysql implementation of ConfigInfoMapper.
 *
 *  @author fuhouyu
 */
public class ConfigInfoMapperByPostgres extends AbstractMapperByPostgres implements ConfigInfoMapper {

    @Override
    public String getDataSource() {
        return DataSourceConstant.POSTGRESQL;
    }

    @Override
    public MapperResult findConfigInfoByAppFetchRows(MapperContext context) {
        String appName = (String) context.getWhereParameter("app_name");
        String tenantId = (String) context.getWhereParameter("tenantId");
        String sql = "SELECT id,data_id,group_id,tenant_id,app_name,content FROM config_info " +
                "WHERE tenant_id LIKE ? AND app_name= ? OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize();
        return new MapperResult(sql, CollectionUtils.list(tenantId, appName));
    }

    @Override
    public MapperResult getTenantIdList(MapperContext mapperContext) {
        String sql = "SELECT tenant_id FROM config_info WHERE tenant_id != '" + NamespaceUtil.getNamespaceDefaultId() + "' GROUP BY tenant_id " +
                "OFFSET " + mapperContext.getStartRow() + " LIMIT " + mapperContext.getPageSize();
        return new MapperResult(sql, Collections.emptyList());
    }

    @Override
    public MapperResult getGroupIdList(MapperContext mapperContext) {
        String sql = "SELECT group_id FROM config_info WHERE tenant_id ='" + NamespaceUtil.getNamespaceDefaultId() + "' GROUP BY group_id " +
                "OFFSET " + mapperContext.getStartRow() + " LIMIT " + mapperContext.getPageSize();
        return new MapperResult(sql, Collections.emptyList());
    }

    @Override
    public MapperResult findAllConfigKey(MapperContext context) {
        String sql = " SELECT data_id,group_id,app_name  FROM (  SELECT id FROM config_info WHERE tenant_id LIKE ? ORDER BY id " +
                "OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize() + " ) g, config_info t WHERE g.id = t.id  ";
        return new MapperResult(sql, CollectionUtils.list(context.getWhereParameter("tenantId")));
    }

    @Override
    public MapperResult findAllConfigInfoBaseFetchRows(MapperContext context) {
        String sql = "SELECT t.id,data_id,group_id,content,md5 FROM ( SELECT id FROM config_info ORDER BY id " +
                "OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize() + "  )  g, config_info t  WHERE g.id = t.id ";
        return new MapperResult(sql, Collections.emptyList());
    }

    @Override
    public MapperResult findAllConfigInfoFragment(MapperContext context) {
        String sql = "SELECT id,data_id,group_id,tenant_id,app_name,content,md5,gmt_modified,type,encrypted_data_key FROM config_info WHERE id > ? ORDER BY id ASC " +
                "OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize();
        return new MapperResult(sql, CollectionUtils.list(context.getWhereParameter("id")));
    }

    @Override
    public MapperResult findChangeConfigFetchRows(MapperContext context) {
        String tenant = (String) context.getWhereParameter("tenantId");
        String dataId = (String) context.getWhereParameter("dataId");
        String group = (String) context.getWhereParameter("groupId");
        String appName = (String) context.getWhereParameter("app_name");
        String tenantTmp = StringUtils.isBlank(tenant) ? "" : tenant;
        Timestamp startTime = (Timestamp) context.getWhereParameter("startTime");
        Timestamp endTime = (Timestamp) context.getWhereParameter("endTime");
        List<Object> paramList = new ArrayList();
        String where = " 1=1 ";
        if (!StringUtils.isBlank(dataId)) {
            where = where + " AND data_id LIKE ? ";
            paramList.add(dataId);
        }

        if (!StringUtils.isBlank(group)) {
            where = where + " AND group_id LIKE ? ";
            paramList.add(group);
        }

        if (!StringUtils.isBlank(tenantTmp)) {
            where = where + " AND tenant_id = ? ";
            paramList.add(tenantTmp);
        }

        if (!StringUtils.isBlank(appName)) {
            where = where + " AND app_name = ? ";
            paramList.add(appName);
        }

        if (startTime != null) {
            where = where + " AND gmt_modified >=? ";
            paramList.add(startTime);
        }

        if (endTime != null) {
            where = where + " AND gmt_modified <=? ";
            paramList.add(endTime);
        }

        return new MapperResult("SELECT id,data_id,group_id,tenant_id,app_name,content,type,md5,gmt_modified FROM config_info " +
                "WHERE " + where + " AND id > " + context.getWhereParameter("lastMaxId") + " ORDER BY id ASC OFFSET " + 0 + " LIMIT " + context.getPageSize(), paramList);
    }

    @Override
    public MapperResult listGroupKeyMd5ByPageFetchRows(MapperContext context) {
        String sql = "SELECT t.id,data_id,group_id,tenant_id,app_name,md5,type,gmt_modified,encrypted_data_key " +
                "FROM ( SELECT id FROM config_info ORDER BY id OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize() + " ) g, config_info t WHERE g.id = t.id";
        return new MapperResult(sql, Collections.emptyList());
    }

    @Override
    public MapperResult findConfigInfoBaseLikeFetchRows(MapperContext context) {
        String dataId = (String) context.getWhereParameter("dataId");
        String group = (String) context.getWhereParameter("groupId");
        String content = (String) context.getWhereParameter("content");
        String where = " 1=1 AND tenant_id='" + NamespaceUtil.getNamespaceDefaultId() + "' ";
        List<Object> paramList = new ArrayList<>();
        if (!StringUtils.isBlank(dataId)) {
            where = where + " AND data_id LIKE ? ";
            paramList.add(dataId);
        }

        if (!StringUtils.isBlank(group)) {
            where = where + " AND group_id LIKE ";
            paramList.add(group);
        }

        if (!StringUtils.isBlank(content)) {
            where = where + " AND content LIKE ? ";
            paramList.add(content);
        }

        return new MapperResult("SELECT id,data_id,group_id,tenant_id,content FROM config_info " +
                "WHERE " + where + " OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize(), paramList);
    }

    @Override
    public MapperResult findConfigInfo4PageFetchRows(MapperContext context) {
        String tenant = (String) context.getWhereParameter("tenantId");
        String dataId = (String) context.getWhereParameter("dataId");
        String group = (String) context.getWhereParameter("groupId");
        String appName = (String) context.getWhereParameter("app_name");
        String content = (String) context.getWhereParameter("content");
        List<Object> paramList = new ArrayList<>();
        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(" tenant_id=? ");
        paramList.add(tenant);
        if (StringUtils.isNotBlank(dataId)) {
            where.append(" AND data_id=? ");
            paramList.add(dataId);
        }

        if (StringUtils.isNotBlank(group)) {
            where.append(" AND group_id=? ");
            paramList.add(group);
        }

        if (StringUtils.isNotBlank(appName)) {
            where.append(" AND app_name=? ");
            paramList.add(appName);
        }

        if (!StringUtils.isBlank(content)) {
            where.append(" AND content LIKE ? ");
            paramList.add(content);
        }

        return new MapperResult("SELECT id,data_id,group_id,tenant_id,app_name,content,type,encrypted_data_key " +
                "FROM config_info" + where + " OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize(), paramList);
    }

    @Override
    public MapperResult findConfigInfoBaseByGroupFetchRows(MapperContext context) {
        String sql = "SELECT id,data_id,group_id,content FROM config_info WHERE group_id=? AND tenant_id=? OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize();
        return new MapperResult(sql, CollectionUtils.list(context.getWhereParameter("groupId"), context.getWhereParameter("tenantId")));
    }

    @Override
    public MapperResult findConfigInfoLike4PageFetchRows(MapperContext context) {
        String tenant = (String) context.getWhereParameter("tenantId");
        String dataId = (String) context.getWhereParameter("dataId");
        String group = (String) context.getWhereParameter("groupId");
        String appName = (String) context.getWhereParameter("app_name");
        String content = (String) context.getWhereParameter("content");
        List<Object> paramList = new ArrayList();
        StringBuilder where = new StringBuilder(" WHERE ");
        where.append(" tenant_id LIKE ? ");
        paramList.add(tenant);
        if (!StringUtils.isBlank(dataId)) {
            where.append(" AND data_id LIKE ? ");
            paramList.add(dataId);
        }

        if (!StringUtils.isBlank(group)) {
            where.append(" AND group_id LIKE ? ");
            paramList.add(group);
        }

        if (!StringUtils.isBlank(appName)) {
            where.append(" AND app_name = ? ");
            paramList.add(appName);
        }

        if (!StringUtils.isBlank(content)) {
            where.append(" AND content LIKE ? ");
            paramList.add(content);
        }

        return new MapperResult("SELECT id,data_id,group_id,tenant_id,app_name,content,encrypted_data_key FROM config_info" + where + " OFFSET " + context.getStartRow() + " LIMIT " + context.getPageSize(), paramList);
    }

    @Override
    public MapperResult findAllConfigInfoFetchRows(MapperContext context) {
        String sql = "SELECT t.id,data_id,group_id,tenant_id,app_name,content,md5  FROM (  SELECT id FROM config_info WHERE tenant_id LIKE ? ORDER BY id LIMIT ?,? ) g, config_info t  WHERE g.id = t.id ";
        return new MapperResult(sql, CollectionUtils.list(context.getWhereParameter("tenantId"), context.getStartRow(), context.getPageSize()));
    }
}
