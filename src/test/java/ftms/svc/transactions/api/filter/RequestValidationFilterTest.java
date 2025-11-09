package ftms.svc.transactions.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class RequestValidationFilterTest {

    @Test
    void shouldRejectRequestWithoutCorrelationId() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var filterChain = mock(FilterChain.class);

        when(request.getHeader("X-Correlation-Id")).thenReturn(null);

        var filter = new RequestValidationFilter();
        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldAllowRequestWithCorrelationId() throws Exception {
        var request = mock(HttpServletRequest.class);
        var response = mock(HttpServletResponse.class);
        var filterChain = mock(FilterChain.class);

        when(request.getHeader("X-Correlation-Id")).thenReturn("abc123");

        var filter = new RequestValidationFilter();
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }
}
