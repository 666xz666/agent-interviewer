package com.agentpioneer.pojo.bo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuthBO {
    private String username;
    private String password;
    private String email;
}
