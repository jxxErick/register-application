package com.register.application.system.service;

import com.register.application.system.domain.Endereco;
import com.register.application.system.domain.Usuario;
import com.register.application.system.dto.UsuarioDTO;
import com.register.application.system.dto.UsuarioNewDTO;
import com.register.application.system.repositories.EnderecoRepository;
import com.register.application.system.repositories.UsuarioRepository;
import com.register.application.system.service.exceptions.DataIntegrityException;
import com.register.application.system.service.exceptions.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private EnderecoRepository enderecoRepository;

    public Usuario find(Integer id) {
        Optional<Usuario> obj = repo.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Usuario.class.getName()));
    }

    @Transactional
    public Usuario insert(Usuario obj) {
        obj.setId(null);
        obj = repo.save(obj);
        enderecoRepository.saveAll(obj.getEnderecos());
        return obj;
    }

    public Usuario update(Usuario obj) {
        Usuario newObj = find(obj.getId());
        updateData(newObj, obj);
        return repo.save(newObj);
    }

    public void delete(Integer id) {
        find(id);
        try {
            repo.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DataIntegrityException("Não é possível excluir");
        }
    }

    public List<Usuario> findAll() {
        return repo.findAll();
    }

    public Page<Usuario> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        return repo.findAll(pageRequest);
    }

    public Usuario fromDTO(UsuarioDTO objDto) {
        return new Usuario(objDto.getId(), objDto.getNome(), null, objDto.getCpf());
    }

    public Usuario fromDTO(UsuarioNewDTO objDto) {
        Usuario cli = new Usuario(null, objDto.getNome(),  objDto.getDataDeNascimento(), objDto.getCpf());
        Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), objDto.getCidade(), objDto.getEstado(), cli);
        cli.getEnderecos().add(end);

        return cli;
    }

    private void updateData(Usuario newObj, Usuario obj) {
        newObj.setNome(obj.getNome());
        newObj.setCpf(obj.getCpf());
    }


}