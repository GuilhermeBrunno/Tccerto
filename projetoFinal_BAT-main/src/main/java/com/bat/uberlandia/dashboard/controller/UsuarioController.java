package com.bat.uberlandia.dashboard.controller;

import com.bat.uberlandia.dashboard.model.Chamado;
import com.bat.uberlandia.dashboard.model.Setor;
import com.bat.uberlandia.dashboard.model.Usuario;
import com.bat.uberlandia.dashboard.repository.ChamadoRepository;
import com.bat.uberlandia.dashboard.repository.SetorRepository;
import com.bat.uberlandia.dashboard.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final ChamadoRepository chamadoRepository;
    private final SetorRepository setorRepository;

    @GetMapping
    public String listar(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<Chamado.Status> statusAtivos = List.of(
                Chamado.Status.EM_ANDAMENTO, Chamado.Status.PAUSADO, Chamado.Status.ESCALADO);

        List<Map<String, Object>> dados = new ArrayList<>();
        for (Usuario u : usuarios) {
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("id", u.getId());
            info.put("nome", u.getNome());
            info.put("login", u.getLogin());
            info.put("tipo", u.getTipo().name());
            info.put("setor", u.getSetor() != null ? u.getSetor().getNome() : "Sem setor");

            long chamadosAtivos = chamadoRepository
                    .findByTecnicoIdAndStatusIn(u.getId(), statusAtivos).size();
            long concluidos = chamadoRepository
                    .countByTecnicoIdAndStatus(u.getId(), Chamado.Status.CONCLUIDO);

            info.put("chamadosAtivos", chamadosAtivos);
            info.put("concluidos", concluidos);
            dados.add(info);
        }

        model.addAttribute("usuarios", dados);
        return "usuarios";
    }

    @GetMapping("/novo")
    public String formNovo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("tipos", Usuario.Tipo.values());
        model.addAttribute("setores", setorRepository.findAll());
        return "usuarios-form";
    }

    @PostMapping("/novo")
    public String criar(@Valid @ModelAttribute Usuario usuario,
                        BindingResult result,
                        @RequestParam Long setorId,
                        RedirectAttributes redirect,
                        Model model) {

        if (usuarioRepository.findByLogin(usuario.getLogin()).isPresent()) {
            result.rejectValue("login", "duplicado", "Login ja cadastrado");
        }

        if (result.hasErrors()) {
            model.addAttribute("tipos", Usuario.Tipo.values());
            model.addAttribute("setores", setorRepository.findAll());
            return "usuarios-form";
        }

        usuario.setSenha("{noop}" + usuario.getSenha());

        if (setorId > 0) {
            Setor setor = setorRepository.findById(setorId).orElse(null);
            usuario.setSetor(setor);
        }

        usuarioRepository.save(usuario);
        redirect.addFlashAttribute("sucesso", "Usuario " + usuario.getNome() + " cadastrado!");
        return "redirect:/usuarios";
    }
}
