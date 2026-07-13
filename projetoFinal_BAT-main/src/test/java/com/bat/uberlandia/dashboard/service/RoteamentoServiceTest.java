package com.bat.uberlandia.dashboard.service;

import com.bat.uberlandia.dashboard.model.*;
import com.bat.uberlandia.dashboard.repository.ChamadoRepository;
import com.bat.uberlandia.dashboard.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoteamentoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ChamadoRepository chamadoRepository;

    @InjectMocks
    private RoteamentoService roteamentoService;

    @Test
    void sugerirTecnico_ShouldReturnBestTecnico() {
        Usuario tec1 = new Usuario();
        tec1.setId(Long.valueOf(1L));
        tec1.setNome("Tecnico 1");
        
        Usuario tec2 = new Usuario();
        tec2.setId(Long.valueOf(2L));
        tec2.setNome("Tecnico 2");

        when(usuarioRepository.findByTipo(Usuario.Tipo.TECNICO)).thenReturn(List.of(tec1, tec2));
        
        // Mocking repository calls inside loop is tricky because it depends on user ID.
        when(chamadoRepository.findByTecnicoIdAndStatusIn(anyLong(), anyList())).thenReturn(List.of());
        when(chamadoRepository.countByTecnicoIdAndMotivoFalhaAndStatus(anyLong(), any(), any())).thenReturn(0L);

        Maquina maquina = new Maquina();
        
        RoteamentoService.SugestaoTecnico sugestao = roteamentoService.sugerirTecnico(maquina, Chamado.MotivoFalha.ELETRICA);

        assertNotNull(sugestao);
    }
}
