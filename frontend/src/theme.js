import { createTheme } from '@mantine/core';

export const theme = createTheme({
  colorScheme: 'dark',
  colors: {
    dark: [
      '#C1C2C5',
      '#A6A7AB',
      '#909296',
      '#5C5F66',
      '#373A40',
      '#2C2E33',
      '#25262B',
      '#1A1B1E',
      '#141517',
      '#101113',
    ],
    neon: [
      '#E3F9FF',
      '#C1F1FF',
      '#9DE8FF',
      '#7ADFFF',
      '#56D6FF',
      '#32CDFF',
      '#0EC4FF',
      '#00A9E0',
      '#008FC0',
      '#00749F',
    ],
    cyber: [
      '#F0E6FF',
      '#D9C2FF',
      '#C19DFF',
      '#AA79FF',
      '#9254FF',
      '#7B30FF',
      '#6A1BFF',
      '#5A00E6',
      '#4A00BF',
      '#3A0099',
    ],
  },
  primaryColor: 'neon',
  primaryShade: 6,
  fontFamily: "'Space Grotesk', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif",
  fontFamilyMonospace: "'JetBrains Mono', 'Courier New', monospace",
  defaultRadius: 'md',
  components: {
    Button: {
      styles: (theme) => ({
        root: {
          transition: 'all 0.3s ease',
          borderRadius: theme.radius.sm,
          textTransform: 'uppercase',
          letterSpacing: '0.5px',
          fontWeight: 600,
          '&:hover': {
            transform: 'translateY(-2px)',
            boxShadow: '0 5px 15px rgba(14, 196, 255, 0.4)',
          },
        },
      }),
    },
    Card: {
      styles: (theme) => ({
        root: {
          backgroundColor: 'rgba(26, 27, 30, 0.8)',
          backdropFilter: 'blur(10px)',
          border: '1px solid rgba(14, 196, 255, 0.2)',
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.3)',
        },
      }),
    },
    Paper: {
      styles: (theme) => ({
        root: {
          backgroundColor: 'rgba(26, 27, 30, 0.6)',
          backdropFilter: 'blur(10px)',
          border: '1px solid rgba(14, 196, 255, 0.1)',
        },
      }),
    },
    Modal: {
      styles: (theme) => ({
        content: {
          backgroundColor: 'rgba(26, 27, 30, 0.95)',
          backdropFilter: 'blur(20px)',
          border: '1px solid rgba(14, 196, 255, 0.3)',
        },
        header: {
          backgroundColor: 'transparent',
        },
      }),
    },
    Table: {
      styles: (theme) => ({
        root: {
          '& thead tr': {
            borderBottom: '2px solid rgba(14, 196, 255, 0.3)',
          },
          '& tbody tr': {
            borderBottom: '1px solid rgba(14, 196, 255, 0.1)',
            transition: 'all 0.2s ease',
            '&:hover': {
              backgroundColor: 'rgba(14, 196, 255, 0.05)',
            },
          },
        },
      }),
    },
    Badge: {
      styles: (theme) => ({
        root: {
          textTransform: 'uppercase',
          letterSpacing: '0.5px',
          fontWeight: 600,
        },
      }),
    },
    NumberInput: {
      styles: (theme) => ({
        input: {
          backgroundColor: 'rgba(26, 27, 30, 0.6)',
          border: '1px solid rgba(14, 196, 255, 0.2)',
          '&:focus': {
            borderColor: theme.colors.neon[6],
            boxShadow: `0 0 0 2px ${theme.colors.neon[6]}20`,
          },
        },
      }),
    },
    TextInput: {
      styles: (theme) => ({
        input: {
          backgroundColor: 'rgba(26, 27, 30, 0.6)',
          border: '1px solid rgba(14, 196, 255, 0.2)',
          '&:focus': {
            borderColor: theme.colors.neon[6],
            boxShadow: `0 0 0 2px ${theme.colors.neon[6]}20`,
          },
        },
      }),
    },
  },
});