package com.arthurmatosbsb.agendabsb.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.arthurmatosbsb.agendabsb.models.Contato;
import com.arthurmatosbsb.agendabsb.models.repository.ContatoRepository;import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

@RestController
@RequestMapping("/contatos")
@CrossOrigin("*")
public class ContatoController {

	@Autowired
	private ContatoRepository contatoRepository;

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public Contato save(@RequestBody Contato contato) {
		return contatoRepository.save(contato);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@PathVariable Integer id) {
	//	Contato c = contatoRepository.findById(id);
		contatoRepository.findById(id).map(c ->{
			contatoRepository.delete(c);
			return Void.TYPE;
				}).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ERRO"));
	}

	@PatchMapping("/{id}/favorito")
	public void favorito(@PathVariable Integer id) {
		Optional<Contato> contato = contatoRepository.findById(id);
		contato.ifPresent(c -> {
			boolean favorito = c.getFavorito() == Boolean.TRUE;
			c.setFavorito(favorito);
			contatoRepository.save(c);
		});
	}

	@GetMapping
	public Page<Contato> list(@RequestParam (value="page", defaultValue = "0") Integer pagina,
			@RequestParam (value="size", defaultValue = "9") Integer tamnhoPagina) {
		PageRequest pr = PageRequest.of(pagina, tamnhoPagina);
		return contatoRepository.findAll(pr);
	}

	@PutMapping("{id}/foto")
	public byte[] addfoto(@PathVariable Integer id, @RequestParam("foto") Part part) {
		Optional<Contato> contato = contatoRepository.findById(id);
		return contato.map(c -> {
			try {
				InputStream input = part.getInputStream();
				byte[] bytes = new byte[(int) part.getSize()];
				IOUtils.readFully(input, bytes);
				c.setFoto(bytes);
				contatoRepository.save(c);
				input.close();
				return bytes;
			} catch (IOException e) {
				return null;
			}
		}).orElse(null);

	}
}
