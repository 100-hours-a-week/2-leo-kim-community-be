package entity.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "profile_pic")
    private String profilePic;

    // Builder 어노테이션 방식
    @Builder
    public UserEntity(String email, String nickname, String password, String profilePic) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profilePic = profilePic;
    }

    /*
    Builder 클래스를 만드는 방식
    static public class Builder {
        private Long id;
        private String email;
        private String password;

        public Builder() {
        }

        public Builder(User user) {
            this.id = user.id;
            this.email = user.email;
            this.password = user.password;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            return new User(id, email, password);
        }
    }
    */

}
