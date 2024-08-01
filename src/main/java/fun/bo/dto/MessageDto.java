package fun.bo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author todoitbo
 * @date 2023/12/20
 */
@Data
public class MessageDto implements Serializable {

    private String key;

    private String message;
}
