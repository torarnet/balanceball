// Uniforms passed from your application code MUST be named m_<param>.
uniform vec4 m_AmbientColor;
uniform vec4 m_DiffuseColor;

// These varyings are set in the vertex shader, and interpolated
varying vec3 normal;
varying vec3 vertexToLightVector;

void main() {
    // We need to normalize the vectors
    vec3 normalizedNormal = normalize(normal);
    vec3 normalizedVertexToLightVector = normalize(vertexToLightVector);

    // Diffuse term is calculated by taking the dot product between the normal for this
    // pixel and the direction of the light
    float diffuseTerm = clamp(dot(normal, normalizedVertexToLightVector), 0.0, 1.0);

    // Color of the pixel is set as the ambient and the diffuse color scaled by the diffuse term
    gl_FragColor = m_AmbientColor + m_DiffuseColor * diffuseTerm;
}