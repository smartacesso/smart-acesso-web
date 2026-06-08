package br.com.startjob.acesso.api;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.com.startjob.acesso.to.app.ApiErrorResponse;

final class AppApiResponses {

	private AppApiResponses() {
	}

	static Response jsonError(Status status, String message, String code) {
		return Response.status(status)
				.entity(new ApiErrorResponse(message, code))
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}
