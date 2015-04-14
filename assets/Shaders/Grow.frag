uniform sampler2D m_Image;
varying vec2 texCoord;

void main() {
    vec4 color = vec4(1.0);

    #ifdef HAS_IMAGE
        color = texture2D(m_Image, texCoord);
    #endif

    gl_FragColor = color;
}