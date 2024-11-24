package com.esprit.security.Model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    private User user;
    private Mail mail;
}
