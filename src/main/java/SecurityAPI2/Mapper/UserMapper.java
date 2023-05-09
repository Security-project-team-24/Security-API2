package SecurityAPI2.Mapper;

import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    List<UserDto> usersToUserDtos(List<User> user);
    User userDtoToUser(UserDto userDto);

    UserDto userToUserDto(User user);
}