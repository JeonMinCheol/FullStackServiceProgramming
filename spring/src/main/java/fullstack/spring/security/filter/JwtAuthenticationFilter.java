package fullstack.spring.security.filter;

import fullstack.spring.entity.User;
import fullstack.spring.repository.UserRepo;
import fullstack.spring.security.service.JwtService;
import fullstack.spring.security.service.UserDetailsServiceImp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

// filter : 매 요청마다 실행 (필터가 두 번 실행될 수 있다.)
// OncePerRequestFilter : 사용자의 요청마다 단 한 번만 실행
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsServiceImp userDetails;
    private final JwtService jwtService;
    private final UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader("Authorization");
        final Optional<String> jwt;

        // 로그인, 회원 가입은 토큰 확인 무시
        if(request.getRequestURI().endsWith("/login") || request.getRequestURI().endsWith("/register") || request.getRequestURI().endsWith("/users")) {
            doFilter(request,response,filterChain);

            // 필터 거쳐서 서블릿 같다가 지금 여기로 돌아옴.
            // 그러므로 return을 써서 더 이상 실행되지 않게 막아줌.

            return;
        }

        // 토큰이 잘못된 경우
        if(authorization == null || !authorization.startsWith("Bearer ")){
            throw new RuntimeException();
        }

        jwt = jwtService.extractToken(authorization);

        if(jwt.isPresent()){
            Optional<User> user = userRepo.findByEmail(jwtService.tokenToEmail(jwt.get()));

            // 인증되지 않은 경우에 인증 처리 (ContextHolder -> Context -> Authentication(userNamePasswordToken 등록))
            if (user.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                try {
                    context.setAuthentication(jwtService.getAuthentication(userDetails.loadUserByUsername(user.get().getEmail())));
                    SecurityContextHolder.setContext(context);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if(user.isEmpty()) {
                throw new UsernameNotFoundException("등록되지 않은 이메일입니다.");
            }
        }

        log.info(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        doFilter(request,response,filterChain);
    }
}
