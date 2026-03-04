package com.was.pojo.vo;


import com.was.pojo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginVO implements Serializable {

    private Long id;

    private String userName;

    private String token;
    
    private String email;
    
    private String nickname;
    
    private String avatar;
    
    private List<Role> roles;

}