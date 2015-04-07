uniform mat4 g_WorldViewProjectionMatrix;
// Converts from model space to view space
uniform mat4 g_WorldViewMatrix;
// Converts normals from model space to view space
uniform mat3 g_WorldMatrixInverseTranspose;
attribute vec3 inPosition;
// normal for the vertex in model space
attribute vec3 inNormal;

// This is the position of the light in view space
uniform vec4 g_LightPosition;

// These are the varyings we will pass to the fragment shader:
// This is the normal for the vertex in view space
varying vec3 normal;
// This is the direction for the vertex to the light
varying vec3 vertexToLightVector;

void main() {
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);

    // Transform the normal to view space
    normal = g_WorldMatrixInverseTranspose * inNormal;

    // If the fourth component of the position is -1 it is a directional light
    if(g_LightPosition.w == -1.0) {
        // The vector from the vertex to the light is the inverse of the direction
        // of the directional light
        vertexToLightVector = vec3(g_LightPosition) * -1.0;
    } else {
        // Transform the vertex from model space to view space
        vec4 vertexInViewSpace = g_WorldViewMatrix * vec4(inPosition, 1.0);
        vertexToLightVector = vec3(g_LightPosition - vertexInViewSpace);
    }
}