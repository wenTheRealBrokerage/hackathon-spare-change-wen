import { useState, useEffect } from 'react'
import { Modal, Button, Stack, Text, Title, Card, Group, Badge, Box, Progress, List, ThemeIcon } from '@mantine/core'
import { IconRocket, IconBolt, IconCheck, IconArrowRight, IconCurrencyBitcoin } from '@tabler/icons-react'
import confetti from 'canvas-confetti'

function OnboardingTutorial({ onComplete }) {
  const [isOpen, setIsOpen] = useState(false)
  const [currentStep, setCurrentStep] = useState(0)
  const [hasSeenTutorial, setHasSeenTutorial] = useState(true)

  useEffect(() => {
    // Check if user has seen tutorial
    const seen = localStorage.getItem('spareChangeTutorialCompleted')
    if (!seen) {
      setHasSeenTutorial(false)
      setIsOpen(true)
    }
  }, [])

  const completeTutorial = () => {
    localStorage.setItem('spareChangeTutorialCompleted', 'true')
    setIsOpen(false)
    if (onComplete) onComplete()
    
    // Celebrate completion
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 }
    })
  }

  const steps = [
    {
      title: "🎉 Welcome to Spare Change!",
      content: (
        <Stack gap="md">
          <Text size="lg">
            Turn your everyday purchases into cryptocurrency investments automatically!
          </Text>
          <List spacing="md" size="md">
            <List.Item icon={
              <ThemeIcon color="neon" size={24} radius="xl">
                <IconCurrencyBitcoin size={16} />
              </ThemeIcon>
            }>
              <strong>Round up</strong> transactions to the nearest dollar
            </List.Item>
            <List.Item icon={
              <ThemeIcon color="neon" size={24} radius="xl">
                <IconRocket size={16} />
              </ThemeIcon>
            }>
              <strong>Automatically invest</strong> spare change in Bitcoin or Ethereum
            </List.Item>
            <List.Item icon={
              <ThemeIcon color="neon" size={24} radius="xl">
                <IconCheck size={16} />
              </ThemeIcon>
            }>
              <strong>Track progress</strong> with real-time updates
            </List.Item>
          </List>
        </Stack>
      )
    },
    {
      title: "🛍️ Let's Try It Out!",
      content: (
        <Stack gap="md">
          <Text size="lg">
            First, let's simulate some purchases by adding random transactions.
          </Text>
          <Card p="lg" style={{
            background: 'linear-gradient(135deg, rgba(14, 196, 255, 0.1) 0%, rgba(106, 27, 255, 0.1) 100%)',
            border: '1px solid rgba(14, 196, 255, 0.3)',
          }}>
            <Group position="apart" align="center">
              <div>
                <Text size="sm" weight={500} color="dimmed">Your Task:</Text>
                <Text size="xl" weight={700}>Click "RANDOM TX" 5-10 times</Text>
              </div>
              <IconBolt size={48} style={{ color: '#0EC4FF', opacity: 0.8 }} />
            </Group>
          </Card>
          <Text size="sm" c="dimmed">
            Each transaction simulates a real purchase and calculates spare change automatically.
          </Text>
        </Stack>
      )
    },
    {
      title: "💰 Check Your Progress",
      content: (
        <Stack gap="md">
          <Text size="lg">
            Great! Now let's see if you've accumulated enough spare change.
          </Text>
          <Card p="lg" style={{
            background: 'linear-gradient(135deg, rgba(14, 196, 255, 0.1) 0%, rgba(106, 27, 255, 0.1) 100%)',
            border: '1px solid rgba(14, 196, 255, 0.3)',
          }}>
            <Group position="apart" align="center">
              <div>
                <Text size="sm" weight={500} color="dimmed">Your Task:</Text>
                <Text size="xl" weight={700}>Click "CHECK THRESHOLD"</Text>
              </div>
              <IconRocket size={48} style={{ color: '#6A1BFF', opacity: 0.8 }} />
            </Group>
          </Card>
          <Text size="sm" c="dimmed">
            When your spare change reaches $5 (default threshold), Bitcoin or Ethereum is automatically purchased!
          </Text>
        </Stack>
      )
    },
    {
      title: "🚀 You're All Set!",
      content: (
        <Stack gap="md">
          <Text size="lg">
            Congratulations! You now know how to use Spare Change.
          </Text>
          <List spacing="sm" size="md">
            <List.Item>Add transactions manually or connect your bank (coming soon)</List.Item>
            <List.Item>Switch between Bitcoin and Ethereum in the cryptocurrency selector</List.Item>
            <List.Item>Adjust your threshold by clicking the edit icon</List.Item>
            <List.Item>View your investment history in the Crypto Orders tab</List.Item>
          </List>
          <Card p="md" style={{
            background: 'linear-gradient(135deg, rgba(247, 147, 26, 0.1) 0%, rgba(98, 126, 234, 0.1) 100%)',
            border: '1px solid rgba(247, 147, 26, 0.3)',
          }}>
            <Text align="center" weight={600}>
              Happy investing! 🎊
            </Text>
          </Card>
        </Stack>
      )
    }
  ]

  const nextStep = () => {
    if (currentStep < steps.length - 1) {
      setCurrentStep(currentStep + 1)
    } else {
      completeTutorial()
    }
  }

  const skipTutorial = () => {
    localStorage.setItem('spareChangeTutorialCompleted', 'true')
    setIsOpen(false)
  }

  if (hasSeenTutorial) return null

  return (
    <Modal
      opened={isOpen}
      onClose={() => {}}
      size="lg"
      centered
      withCloseButton={false}
      styles={{
        content: {
          backgroundColor: 'rgba(26, 27, 30, 0.98)',
          backdropFilter: 'blur(20px)',
          border: '1px solid rgba(14, 196, 255, 0.3)',
          boxShadow: '0 0 40px rgba(14, 196, 255, 0.3)',
        },
        header: {
          backgroundColor: 'transparent',
        },
      }}
    >
      <Stack gap="xl">
        <div>
          <Title order={2} style={{ marginBottom: '0.5rem' }}>
            {steps[currentStep].title}
          </Title>
          <Progress 
            value={(currentStep / (steps.length - 1)) * 100} 
            color="neon" 
            size="xs"
            styles={{
              bar: {
                backgroundImage: 'linear-gradient(90deg, #0EC4FF 0%, #6A1BFF 100%)',
              }
            }}
          />
        </div>

        <Box style={{ minHeight: '250px' }}>
          {steps[currentStep].content}
        </Box>

        <Group position="apart">
          <Button 
            variant="subtle" 
            color="gray" 
            onClick={skipTutorial}
            size="md"
          >
            Skip Tutorial
          </Button>
          <Group>
            {currentStep > 0 && (
              <Button 
                variant="light" 
                onClick={() => setCurrentStep(currentStep - 1)}
                size="md"
              >
                Back
              </Button>
            )}
            <Button
              rightSection={currentStep === steps.length - 1 ? <IconCheck size={16} /> : <IconArrowRight size={16} />}
              onClick={nextStep}
              size="md"
              styles={{
                root: {
                  background: 'linear-gradient(135deg, rgba(14, 196, 255, 0.2) 0%, rgba(106, 27, 255, 0.2) 100%)',
                  border: '1px solid rgba(14, 196, 255, 0.5)',
                  '&:hover': {
                    transform: 'translateY(-2px)',
                    boxShadow: '0 5px 15px rgba(14, 196, 255, 0.4)',
                  }
                }
              }}
            >
              {currentStep === steps.length - 1 ? 'Get Started!' : 'Next'}
            </Button>
          </Group>
        </Group>
      </Stack>
    </Modal>
  )
}

export default OnboardingTutorial