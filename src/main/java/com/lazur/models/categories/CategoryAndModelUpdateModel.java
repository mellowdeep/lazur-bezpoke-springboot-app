package com.lazur.models.categories;


import com.lazur.validations.IsOldAndNewCodeNull;
import org.hibernate.validator.constraints.NotBlank;

@IsOldAndNewCodeNull
public class CategoryAndModelUpdateModel {

    private Long id;

    @NotBlank(message = "Field should not be empty")
    private String name;

    private String oldCode;

    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
