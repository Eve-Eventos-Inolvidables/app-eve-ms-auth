package com.example.appevemsauth.Service.User;


import com.example.appevecommon.Service.Utilities.Responses.PagedFilter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilter extends PagedFilter {
    private String name;
}