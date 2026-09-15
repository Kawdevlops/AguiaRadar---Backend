package br.com.fiap.aguiaradar.config;

import br.com.fiap.aguiaradar.model.Perfil;
import br.com.fiap.aguiaradar.model.Usuario;
import br.com.fiap.aguiaradar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Cria os 3 perfis de usuario (operador, gestor e lideranca) na primeira
 * inicializacao, para facilitar os testes/demo do desafio.
 * Pode ser desabilitado via aguiaradar.seed.enabled=false.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${aguiaradar.seed.enabled:true}")
    private boolean seedEnabled;

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }

        criarSeUsuarioNaoExiste("Ana Operadora", "operador@aguiaradar.com", "123456", Perfil.OPERADOR, 1);
        criarSeUsuarioNaoExiste("Bruno Gestor", "gestor@aguiaradar.com", "123456", Perfil.GESTOR, 1);
        criarSeUsuarioNaoExiste("Carla Lideranca", "lideranca@aguiaradar.com", "123456", Perfil.LIDERANCA, null);

        log.info("=== AguiaRadar: usuarios de demonstracao prontos (senha: 123456) ===");
    }

    private void criarSeUsuarioNaoExiste(String nome, String email, String senha, Perfil perfil, Integer filialId) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senhaHash(passwordEncoder.encode(senha))
                .perfil(perfil)
                .filialId(filialId)
                .ativo(true)
                .dataCriacao(LocalDateTime.now())
                .build();

        usuarioRepository.save(usuario);
        log.info("Usuario seed criado: {} ({})", email, perfil);
    }
}
