uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;
attribute vec2 inTexCoord;
uniform float g_Time;
uniform float m_Size;

varying vec2 texCoord;

void main() {
    vec3 a = inPosition;
    float scale = (g_Time / 10.0) * m_Size;
    if (scale>3)scale=1.0;
    a.x = a.x * scale;
    a.y = a.y * scale;
    a.z = a.z * scale;
    
    gl_Position = g_WorldViewProjectionMatrix * vec4(a,1.0);
    texCoord = vec2(inTexCoord.x,inTexCoord.y);

}