package edouard.yu.springsecuritylearning.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthorityType {
    ADMINISTRATOR_CREATE,
    ADMINISTRATOR_READ,
    ADMINISTRATOR_UPDATE,
    ADMINISTRATOR_DELETE,

    MANAGER_CREATE,
    MANAGER_READ,
    MANAGER_UPDATE,
    MANAGER_DELETE_POST,

    USER_CREATE_POST,
    USER_READ,
    USER_UPDATE_POST
    ;

    private String label;
}
