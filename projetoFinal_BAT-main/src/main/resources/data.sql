-- ============================================================
-- Seed BAT Uberlandia - dados essenciais do dashboard
-- Idempotente (MERGE por id) - roda a cada inicio sem duplicar
-- ------------------------------------------------------------

-- ============================================================
-- ESTRUTURA DAS TABELAS (mapeadas pelas entidades JPA)
-- ============================================================
--
-- SETORES        -> model/Setor.java        (tabela: setores)
--   id (PK), nome, descricao
--
-- USUARIOS      -> model/Usuario.java        (tabela: usuarios)
--   id (PK), login (unique), senha, nome, tipo (enum), setor_id (FK)
--   tipo: ADMIN | OPERADOR | TECNICO | LIDER | ESPECIALISTA | VISUALIZADOR
--   senha prefixada com {noop} = texto puro (so p/ desenvolvimento)
--
-- MAQUINAS       -> model/Maquina.java       (tabela: maquinas)
--   id (PK), nome (unique), modelo, numero_serie, status (enum), setor_id (FK)
--   status: OPERANDO | PARADA | MANUTENCAO
--
-- CHAMADOS       -> model/Chamado.java       (tabela: chamados)
--   id (PK), titulo, descricao, caminho_foto,
--   status (enum): ABERTO | EM_ANDAMENTO | PAUSADO | ESCALADO | CONCLUIDO
--   motivo_falha (enum): ELETRICA | MECANICA | PNEUMATICA | HIDRAULICA |
--                        ELETRONICA | SOFTWARE | OPERACIONAL | DESGASTE | OUTROS
--   maquina_id (FK, NOT NULL), tecnico_id (FK, nullable),
--   especialista_id (FK, nullable),
--   inicio_reparo, inicio_pausa, tempo_reparo_acumulado (segundos),
--   inicio_locomocao, fim_locomocao, tempo_locomocao_segundos,
--   data_abertura, data_conclusao, data_escalacao,
--   alerta_30min_enviado (bool)
--   -> ABERTO: so maquina_id e data_abertura (tecnico nullable)
--
-- NOTIFICACOES   -> model/Notificacao.java   (tabela: notificacoes)
--   id (PK), tipo (enum): ALERTA_30MIN | ESCALACAO | CONCLUSAO,
--   mensagem, data_envio, lida (bool),
--   chamado_id (FK, NOT NULL), usuario_id (FK, NOT NULL)

-- ============================================================
-- 1) SETORES (5 primeiros IDs fixos)
-- ============================================================
MERGE INTO setores (id, nome, descricao) KEY(id) VALUES (1, 'Producao', 'Linha de producao de cigarros');
MERGE INTO setores (id, nome, descricao) KEY(id) VALUES (2, 'Embalagem', 'Encaixotamento e embalagem secundaria');
MERGE INTO setores (id, nome, descricao) KEY(id) VALUES (3, 'Logistica', 'Esteiras e armazenagem de PA');
MERGE INTO setores (id, nome, descricao) KEY(id) VALUES (4, 'Utilidades', 'Vapor, ar comprimido e climatizacao');
MERGE INTO setores (id, nome, descricao) KEY(id) VALUES (5, 'Manutencao', 'Oficina e almoxarifado tecnico');

-- ============================================================
-- 2) USUARIO ADMIN INICIAL
--    Apenas o ADMIN vem do seed. Ele cadastra todos os outros
--    (tecnicos, operadores, lideres, especialistas) pela tela
--    /usuarios (UsuarioController).
--    Login: lucaslopes  Senha: 123
-- ============================================================
MERGE INTO usuarios (id, login, senha, nome, tipo, setor_id) KEY(id)
VALUES (1, 'lucaslopes', '{noop}123', 'Lucas Lopes', 'ADMIN', 5);

-- ============================================================
-- 3) MAQUINAS (uma por setor, para exemplo)
--    Cada maquina pertence a um setor (FK setor_id)
-- ============================================================
MERGE INTO maquinas (id, nome, modelo, numero_serie, status, setor_id) KEY(id) VALUES (1, 'Embaladora Primaria 01', 'GD-XP', 'EP-001', 'OPERANDO', 1);
MERGE INTO maquinas (id, nome, modelo, numero_serie, status, setor_id) KEY(id) VALUES (2, 'Maquina de Cortar 02', 'MCP-9', 'MC-002', 'OPERANDO', 1);
MERGE INTO maquinas (id, nome, modelo, numero_serie, status, setor_id) KEY(id) VALUES (3, 'Encaixotadora 01', 'EC-2000', 'EX-003', 'OPERANDO', 2);
MERGE INTO maquinas (id, nome, modelo, numero_serie, status, setor_id) KEY(id) VALUES (4, 'Esteira Transportadora 04', 'ET-50', 'ES-004', 'OPERANDO', 3);
MERGE INTO maquinas (id, nome, modelo, numero_serie, status, setor_id) KEY(id) VALUES (5, 'Caldeira de Vapor', 'CV-1000', 'CA-005', 'OPERANDO', 4);
MERGE INTO maquinas (id, nome, modelo, numero_serie, status, setor_id) KEY(id) VALUES (6, 'Compressor de Ar', 'CA-75', 'CP-006', 'OPERANDO', 4);
MERGE INTO maquinas (id, nome, modelo, numero_serie, status, setor_id) KEY(id) VALUES (7, 'Paletizadora 01', 'PL-800', 'PA-007', 'OPERANDO', 2);

-- ============================================================
-- 4) CHAMADOS - sem seed, criados ao vivo via /chamados/novo
--    O ADMIN cadastra tecnicos via /usuarios, depois um novo
--    chamado ABERTO aparecera em /chamados/{id} com a opcao
--    "Assumir e iniciar" para atribuir o tecnico.
-- ============================================================

-- ============================================================
-- 5) NOTIFICACOES - geradas automaticamente pelo AlertaService
--    (reparo > 30 min ou escalacao).
-- ============================================================