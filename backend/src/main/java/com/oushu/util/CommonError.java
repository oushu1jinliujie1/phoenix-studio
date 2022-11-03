package com.oushu.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonError {
    private boolean error;
    private String errorString;

    public static CommonError NonError = new CommonError(false, "");
}
