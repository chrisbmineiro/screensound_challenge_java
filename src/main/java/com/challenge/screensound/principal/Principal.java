package com.challenge.screensound.principal;

import com.challenge.screensound.models.Artista;
import com.challenge.screensound.models.Musica;
import com.challenge.screensound.models.TipoArtista;
import com.challenge.screensound.repository.ArtistaRepository;
import com.challenge.screensound.services.ConsultaChatGPT;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private final ArtistaRepository repository;
    private Scanner leitura = new Scanner(System.in);

    public Principal(ArtistaRepository repository) {
        this.repository = repository;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao!= 0) {
            var menu = """
                    *** Screen Sound Músicas ***                    
                                        
                    1- Cadastrar artistas
                    2- Cadastrar músicas
                    3- Listar músicas
                    4- Buscar músicas por artistas
                    5- Pesquisar dados sobre um artista
                                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    cadastrarArtistas();
                    break;
                case 2:
                    cadastrarMusicas();
                    break;
                case 3:
                    listarMusicas();
                    break;
                case 4:
                    buscarMusicasPorArtista();
                    break;
                case 5:
                    pesquisarDadosDoArtista();
                    break;
                case 9:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void pesquisarDadosDoArtista() {
        System.out.println("Pesquisar dados sobre qual artista: ");
        var nome = leitura.nextLine();
        var response = ConsultaChatGPT.obterInformacao(nome);
        System.out.println(response.trim());
    }

    private void buscarMusicasPorArtista() {
        System.out.println("Buscar musicas de qual artista: ");
        var nome = leitura.nextLine();
        List<Musica> musicas = repository.buscaMusicaPorArtista(nome);
        musicas.forEach(System.out::println);
    }

    private void listarMusicas() {
        List<Artista> artistas = repository.findAll();
        artistas.forEach(a -> a.getMusicas().forEach(System.out::println));
    }

    private void cadastrarMusicas() {
        System.out.println("Cadastrar musica de qual artista: ");
        var nomeArtista = leitura.nextLine();
        Optional<Artista> artista = repository.findByNomeContainingIgnoreCase(nomeArtista);
        if (artista.isPresent()){
            System.out.println("Digite o titulo da música: ");
            var nomeMusica = leitura.nextLine();
            Musica musica = new Musica(nomeMusica);
            musica.setArtista(artista.get());
            artista.get().getMusicas().add(musica);
            repository.save(artista.get());
        } else {
            System.out.println("Artista não encontrado!");
        }
    }

    private void cadastrarArtistas() {
        var cadastrarNovo = "S";
        while (cadastrarNovo.equalsIgnoreCase("s")) {
            System.out.println("Digite o nome do artista");
            var nome = leitura.nextLine();
            System.out.println("Digite o tipo de artista: (solo, dupla ou banda)");
            var tipo = leitura.nextLine();
            TipoArtista tipoArtista = TipoArtista.valueOf(tipo.toUpperCase());
            Artista artista = new Artista(nome, tipoArtista);
            repository.save(artista);
            System.out.println("Cadastrar novo artista? (S/N)");
            cadastrarNovo = leitura.nextLine();
        }
    }
}
