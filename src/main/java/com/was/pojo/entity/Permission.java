package com.was.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_permission")
public class Permission implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String permissionName;

    private String permissionKey;

    private Integer type;

    private Long parentId;

    private String path;

    private String description;

    private LocalDateTime createTime;
}
