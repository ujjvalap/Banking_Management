package com.sprint.bank.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final BankUserDetailsService userDetailsService;
	private final AuthenticationEntryPoint authenticationEntryPoint;

	public JwtAuthenticationFilter(
			JwtService jwtService,
			BankUserDetailsService userDetailsService,
			AuthenticationEntryPoint authenticationEntryPoint) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");
		if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String email = jwtService.extractSubject(authorization.substring(7));
			if (StringUtils.hasText(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				var authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (JwtException | IllegalArgumentException | UsernameNotFoundException exception) {
			SecurityContextHolder.clearContext();
			authenticationEntryPoint.commence(request, response,
					new org.springframework.security.authentication.BadCredentialsException("Invalid or expired token."));
			return;
		}
		filterChain.doFilter(request, response);
	}
}
