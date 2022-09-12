package com.testaarosa.springRecallBookApp.uploads.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder()
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Upload {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String serverFileName;
    private String contentType;
    private LocalDateTime createdAt;
    private String path;

}
