package br.com.fiap.aguiaradar.controller;

import br.com.fiap.aguiaradar.dto.LoginRequest;
import br.com.fiap.aguiaradar.dto.LoginResponse;
import br.com.fiap.aguiaradar.dto.RegistroUsuarioRequest;
import br.com.fiap.aguiaradar.model.Usuario;
import br.com.fiap.aguiaradar.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Cadastro de novos usuarios. Em producao, recomenda-se restringir este
     * endpoint ao perfil LIDERANCA (adicionar @PreAuthorize apos o primeiro
     * usuario lideranca existir); mantido aberto aqui para facilitar os testes
     * do desafio via Postman/app.
     */
    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        Usuario usuario = authService.registrar(request);
        usuario.setSenhaHash(null); // nunca retornar hash de senha
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
}
