package SecurityAPI2.Dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageDto<T> {
    List<T> content;
    int totalPages;
}
