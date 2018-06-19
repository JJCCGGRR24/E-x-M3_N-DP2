/*
 * StringToComodinConverter.java
 * 
 * Copyright (C) 2016 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the
 * TDG Licence, a copy of which you may download from
 * http://www.tdg-seville.info/License.html
 */

package converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import repositories.ComodinRepository;
import domain.Comodin;

@Component
@Transactional
public class StringToComodinConverter implements Converter<String, Comodin> {

	@Autowired
	ComodinRepository	comodinRepository;


	@Override
	public Comodin convert(final String text) {
		Comodin result;
		int id;

		try {
			id = Integer.valueOf(text);
			result = this.comodinRepository.findOne(id);
		} catch (final Exception oops) {
			throw new IllegalArgumentException(oops);
		}

		return result;
	}

}
